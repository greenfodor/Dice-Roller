package com.greenfodor.diceroller.ui.screens

import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollOutcome
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
import java.time.ZoneId

private class FakeRollHistoryRepository : RollHistoryRepository {
    private val state = MutableStateFlow(emptyList<RollRecord>())
    override val rolls = state

    override suspend fun record(record: RollRecord) {
        state.update { it + record }
    }

    override suspend fun clear() {
        state.value = emptyList()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DiceViewModelTest {
    private val clock: Clock = Clock.fixed(Instant.ofEpochMilli(FIXED_MILLIS), ZoneId.of("UTC"))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `a settled single die roll is persisted with its label value and total`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, clock)

        viewModel.onRollSettled(RollOutcome(dieLabel = DieLabels.D20, values = listOf(17), total = 17))

        val stored = repository.rolls.first().single()
        assertEquals(DieLabels.D20, stored.dieLabel)
        assertEquals(listOf(17), stored.values)
        assertEquals(17, stored.total)
    }

    @Test
    fun `a settled two dice roll is persisted as one record holding both values`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, clock)

        viewModel.onRollSettled(RollOutcome(dieLabel = DieLabels.DOUBLE_D6, values = listOf(4, 6), total = 10))

        val stored = repository.rolls.first().single()
        assertEquals(DieLabels.DOUBLE_D6, stored.dieLabel)
        assertEquals(listOf(4, 6), stored.values)
        assertEquals(10, stored.total)
    }

    @Test
    fun `a percentile roll keeps a scored total that differs from the sum of its dice`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, clock)

        viewModel.onRollSettled(RollOutcome(dieLabel = DieLabels.D100, values = listOf(0, 0), total = 100))

        val stored = repository.rolls.first().single()
        assertEquals(listOf(0, 0), stored.values)
        assertEquals(100, stored.total)
    }

    @Test
    fun `a settled roll is stamped with the clock time`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, clock)

        viewModel.onRollSettled(RollOutcome(dieLabel = DieLabels.D6, values = listOf(3), total = 3))

        assertEquals(FIXED_MILLIS, repository.rolls.first().single().timestampMillis)
    }

    @Test
    fun `each settled roll appends another record`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, clock)

        viewModel.onRollSettled(RollOutcome(dieLabel = DieLabels.D4, values = listOf(1), total = 1))
        viewModel.onRollSettled(RollOutcome(dieLabel = DieLabels.D4, values = listOf(2), total = 2))

        assertEquals(listOf(1, 2), repository.rolls.first().map { it.total })
    }

    private companion object {
        const val FIXED_MILLIS = 1_787_000_000_000L
    }
}
