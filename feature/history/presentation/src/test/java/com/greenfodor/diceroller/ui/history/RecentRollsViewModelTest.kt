package com.greenfodor.diceroller.ui.history

import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Holds its rolls newest first, as the Room-backed repository does. */
private class FakeRecentRollsRepository(
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
class RecentRollsViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `recentRolls starts empty`() = runTest {
        val viewModel = RecentRollsViewModel(FakeRecentRollsRepository())

        assertEquals(emptyList<RecentRollUiModel>(), viewModel.recentRolls.value)
    }

    @Test
    fun `recentRolls holds no more than the wheel shows`() = runTest {
        val repository = FakeRecentRollsRepository(
            (1..MORE_THAN_THE_WHEEL_HOLDS).map { index -> roll(id = index.toLong(), timestampMillis = index * 1_000L) }
                .sortedByDescending { it.timestampMillis }
        )
        val viewModel = RecentRollsViewModel(repository)

        assertEquals(RECENT_ROLLS_COUNT, viewModel.recentRolls.first { it.isNotEmpty() }.size)
    }

    @Test
    fun `recentRolls renders the newest roll last`() = runTest {
        val repository = FakeRecentRollsRepository(
            listOf(
                roll(id = 3, dieLabel = DieLabels.D20, total = 20, timestampMillis = 3_000L),
                roll(id = 2, dieLabel = DieLabels.D8, total = 8, timestampMillis = 2_000L),
                roll(id = 1, dieLabel = DieLabels.D4, total = 4, timestampMillis = 1_000L),
                roll(id = 0, dieLabel = DieLabels.D6, total = 6, timestampMillis = 0L)
            )
        )
        val viewModel = RecentRollsViewModel(repository)

        assertEquals(
            listOf("d4 4", "d8 8", "d20 20"),
            viewModel.recentRolls.first { it.isNotEmpty() }.map { it.line() }
        )
    }

    @Test
    fun `recentRolls follows the repository as rolls are appended`() = runTest {
        val repository = FakeRecentRollsRepository()
        val viewModel = RecentRollsViewModel(repository)
        val emissions = mutableListOf<List<RecentRollUiModel>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.recentRolls.toList(emissions) }

        repository.record(roll(id = 1, dieLabel = DieLabels.D6, total = 6, timestampMillis = 1_000L))
        repository.record(roll(id = 2, dieLabel = DieLabels.D20, total = 20, timestampMillis = 2_000L))

        assertEquals(listOf("d6 6", "d20 20"), emissions.last().map { it.line() })
    }

    private fun RecentRollUiModel.line(): String = listOfNotNull(
        dieLabel,
        total,
        breakdown
    ).joinToString(separator = " ")

    private fun roll(
        id: Long,
        dieLabel: String = DieLabels.D6,
        total: Int = 6,
        timestampMillis: Long
    ): RollRecord =
        RollRecord(
            id = id,
            dieLabel = dieLabel,
            values = listOf(total),
            total = total,
            timestampMillis = timestampMillis
        )

    private companion object {
        const val MORE_THAN_THE_WHEEL_HOLDS = RECENT_ROLLS_COUNT + 2
    }
}
