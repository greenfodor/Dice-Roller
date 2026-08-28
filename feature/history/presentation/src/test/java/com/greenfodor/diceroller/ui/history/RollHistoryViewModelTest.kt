package com.greenfodor.diceroller.ui.history

import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

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

@OptIn(ExperimentalCoroutinesApi::class)
class RollHistoryViewModelTest {
    private val zone: ZoneId = ZoneId.of("Europe/Bucharest")
    private val clock: Clock = Clock.fixed(Instant.ofEpochMilli(millisAt(2026, 8, 28, 14, 30)), zone)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState starts as Loading before the repository emits`() {
        val viewModel = RollHistoryViewModel(FakeRollHistoryRepository(), clock)

        assertEquals(RollHistoryUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Empty when the repository holds no rolls`() = runTest {
        val viewModel = RollHistoryViewModel(FakeRollHistoryRepository(), clock)

        assertEquals(RollHistoryUiState.Empty, viewModel.uiState.first { it != RollHistoryUiState.Loading })
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
        val viewModel = RollHistoryViewModel(repository, clock)

        val content = viewModel.uiState.first { it is RollHistoryUiState.Content } as RollHistoryUiState.Content

        assertEquals(
            listOf(
                RollHistoryDay.Today,
                RollHistoryDay.Yesterday,
                RollHistoryDay.Earlier(LocalDate.of(2026, 8, 20))
            ),
            content.sections.map { it.day }
        )
    }

    @Test
    fun `uiState follows the repository as rolls are appended`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = RollHistoryViewModel(repository, clock)
        viewModel.uiState.first { it is RollHistoryUiState.Empty }

        repository.record(roll(dieLabel = DieLabels.D8, timestampMillis = millisAt(2026, 8, 28, 12, 0)))

        val content = viewModel.uiState.first { it is RollHistoryUiState.Content } as RollHistoryUiState.Content
        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Today), content.sections.map { it.day })
        assertEquals(DieLabels.D8, content.sections.single().rolls.single().dieLabel)
    }

    @Test
    fun `uiState returns to Empty once the repository is emptied`() = runTest {
        val repository = FakeRollHistoryRepository(listOf(roll(timestampMillis = millisAt(2026, 8, 28, 10, 0))))
        val viewModel = RollHistoryViewModel(repository, clock)
        viewModel.uiState.first { it is RollHistoryUiState.Content }

        repository.clear()

        assertEquals(RollHistoryUiState.Empty, viewModel.uiState.first { it is RollHistoryUiState.Empty })
    }

    private fun millisAt(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        LocalDateTime.of(year, month, day, hour, minute).atZone(zone).toInstant().toEpochMilli()

    private fun roll(dieLabel: String = DieLabels.D6, timestampMillis: Long): RollRecord =
        RollRecord(dieLabel = dieLabel, values = listOf(4), total = 4, timestampMillis = timestampMillis)
}
