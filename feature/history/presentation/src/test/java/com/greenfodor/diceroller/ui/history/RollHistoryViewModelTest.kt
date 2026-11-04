package com.greenfodor.diceroller.ui.history

import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.Instant

private class FakeRollHistoryRepository(
    initial: List<RollRecord> = emptyList()
) : RollHistoryRepository {
    private val state = MutableStateFlow(initial)
    override val rolls = state

    override suspend fun record(record: RollRecord) {
        state.update { current -> (current + record).sortedByDescending { it.timestampMillis } }
    }

    override suspend fun clear() {
        state.value = emptyList()
    }
}

/** Never emits, so the ViewModel stays on its initial state. */
private class SilentRollHistoryRepository : RollHistoryRepository {
    override val rolls: Flow<List<RollRecord>> = MutableSharedFlow()

    override suspend fun record(record: RollRecord) = Unit

    override suspend fun clear() = Unit
}

/** Fails on the first read, as a Room query on a corrupt database does. */
private class FailingRollHistoryRepository : RollHistoryRepository {
    override val rolls: Flow<List<RollRecord>> = flow { error("read failed") }

    override suspend fun record(record: RollRecord) = Unit

    override suspend fun clear() = Unit
}

/** A [Clock] the test moves forward by hand. */
private class MutableClock(
    var now: Instant
) : Clock {
    override fun now(): Instant = now
}

@OptIn(ExperimentalCoroutinesApi::class)
class RollHistoryViewModelTest {
    private val zone: TimeZone = TimeZone.of("Europe/Bucharest")
    private val clock: Clock = MutableClock(Instant.fromEpochMilliseconds(millisAt(2026, 8, 28, 14, 30)))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState starts as Loading before the repository emits`() = runTest {
        val viewModel = RollHistoryViewModel(SilentRollHistoryRepository(), clock, zone)

        val states = mutableListOf<RollHistoryUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.toList(states) }

        assertEquals(listOf<RollHistoryUiState>(RollHistoryUiState.Loading), states)
    }

    @Test
    fun `uiState becomes Empty when the repository holds no rolls`() = runTest {
        val viewModel = RollHistoryViewModel(FakeRollHistoryRepository(), clock, zone)

        assertEquals(RollHistoryUiState.Empty, viewModel.uiState.first { it != RollHistoryUiState.Loading })
    }

    @Test
    fun `uiState becomes Error when reading the history fails`() = runTest {
        val viewModel = RollHistoryViewModel(FailingRollHistoryRepository(), clock, zone)

        assertEquals(RollHistoryUiState.Error, viewModel.uiState.first { it != RollHistoryUiState.Loading })
    }

    @Test
    fun `uiState becomes Content grouped by day when the repository holds rolls`() = runTest {
        val repository = FakeRollHistoryRepository(
            listOf(
                roll(dieLabel = "d20", timestampMillis = millisAt(2026, 8, 28, 10, 0)),
                roll(dieLabel = "d6", timestampMillis = millisAt(2026, 8, 27, 10, 0)),
                roll(dieLabel = "d4", timestampMillis = millisAt(2026, 8, 20, 10, 0))
            )
        )
        val viewModel = RollHistoryViewModel(repository, clock, zone)

        val content = viewModel.uiState.first { it is RollHistoryUiState.Content } as RollHistoryUiState.Content

        assertEquals(
            listOf(
                RollHistoryDay.Today,
                RollHistoryDay.Yesterday,
                RollHistoryDay.Earlier(LocalDate(2026, 8, 20))
            ),
            content.sections.map { it.day }
        )
    }

    @Test
    fun `uiState follows the repository as rolls are appended`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = RollHistoryViewModel(repository, clock, zone)
        viewModel.uiState.first { it is RollHistoryUiState.Empty }

        repository.record(roll(dieLabel = DieLabels.D8, timestampMillis = millisAt(2026, 8, 28, 12, 0)))

        val content = viewModel.uiState.first { it is RollHistoryUiState.Content } as RollHistoryUiState.Content
        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Today), content.sections.map { it.day })
        assertEquals(DieLabels.D8, content.sections.single().rolls.single().dieLabel)
    }

    @Test
    fun `uiState returns to Empty once the repository is emptied`() = runTest {
        val repository = FakeRollHistoryRepository(listOf(roll(timestampMillis = millisAt(2026, 8, 28, 10, 0))))
        val viewModel = RollHistoryViewModel(repository, clock, zone)
        viewModel.uiState.first { it is RollHistoryUiState.Content }

        repository.clear()

        assertEquals(RollHistoryUiState.Empty, viewModel.uiState.first { it is RollHistoryUiState.Empty })
    }

    @Test
    fun `a rolls day header moves from Today to Yesterday once local midnight passes`() = runTest {
        val repository = FakeRollHistoryRepository(listOf(roll(timestampMillis = millisAt(2026, 8, 28, 23, 0))))
        val movingClock = MutableClock(Instant.fromEpochMilliseconds(millisAt(2026, 8, 28, 23, 30)))
        val viewModel = RollHistoryViewModel(repository, movingClock, zone)
        val states = mutableListOf<RollHistoryUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.toList(states) }
        assertEquals(RollHistoryDay.Today, states.lastDay())

        movingClock.now = Instant.fromEpochMilliseconds(millisAt(2026, 8, 29, 0, 1))
        advanceTimeBy(PAST_MIDNIGHT_MILLIS)

        assertEquals(RollHistoryDay.Yesterday, states.lastDay())
    }

    private fun List<RollHistoryUiState>.lastDay(): RollHistoryDay =
        (last() as RollHistoryUiState.Content).sections.single().day

    private fun millisAt(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        LocalDateTime(year, month, day, hour, minute).toInstant(zone).toEpochMilliseconds()

    private fun roll(dieLabel: String = DieLabels.D6, timestampMillis: Long): RollRecord =
        RollRecord(dieLabel = dieLabel, values = listOf(4), total = 4, timestampMillis = timestampMillis)

    private companion object {
        /** Just over the half hour between the test's starting instant and the next midnight. */
        const val PAST_MIDNIGHT_MILLIS = 31L * 60L * 1000L
    }
}
