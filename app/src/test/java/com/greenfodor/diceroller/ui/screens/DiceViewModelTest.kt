package com.greenfodor.diceroller.ui.screens

import androidx.lifecycle.viewModelScope
import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.data.DiceColorSettings
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.ui.settings.SettingsViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
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

private class FakeRollHistoryRepository : RollHistoryRepository {
    private val state = MutableStateFlow(emptyList<RollRecord>())
    override val rolls = state

    /** Completed by the test to let a suspended [record] finish. */
    var recordGate: CompletableDeferred<Unit>? = null

    override suspend fun record(record: RollRecord) {
        recordGate?.await()
        state.update { it + record }
    }

    override suspend fun clear() {
        state.value = emptyList()
    }
}

/** In-memory [SettingsRepository] so a [SettingsViewModel] can be built without DataStore. */
private class FakeSettingsRepository : SettingsRepository {
    override val themeMode = MutableStateFlow(ThemeMode.FOLLOW_SYSTEM)
    override val hapticFeedbackEnabled = MutableStateFlow(true)
    override val shakeToRollEnabled = MutableStateFlow(true)
    override val d6FaceStyle = MutableStateFlow(D6FaceStyle.PIPS)
    override val diceColorSettings = MutableStateFlow(DiceColorSettings())

    override suspend fun setThemeMode(mode: ThemeMode) {
        themeMode.value = mode
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        hapticFeedbackEnabled.value = enabled
    }

    override suspend fun setShakeToRollEnabled(enabled: Boolean) {
        shakeToRollEnabled.value = enabled
    }

    override suspend fun setD6FaceStyle(style: D6FaceStyle) {
        d6FaceStyle.value = style
    }

    override suspend fun setUseSingleDiceColor(enabled: Boolean) {
        diceColorSettings.update { it.copy(useSingleColor = enabled) }
    }

    override suspend fun setSingleDiceColor(option: DiceColorOption) {
        diceColorSettings.update { it.copy(singleColor = option) }
    }

    override suspend fun setDiceColor(target: DieColorTarget, option: DiceColorOption) {
        diceColorSettings.update { it.copy(perDie = it.perDie + (target to option)) }
    }

    override suspend fun resetDiceColors() {
        diceColorSettings.value = DiceColorSettings()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DiceViewModelTest {
    private lateinit var applicationScope: CoroutineScope

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        applicationScope = CoroutineScope(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        applicationScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `a settled single die roll is persisted with its label value and total`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, applicationScope)

        viewModel.onRollSettled(outcome(dieLabel = DieLabels.D20, values = listOf(17), total = 17))

        val stored = repository.rolls.first().single()
        assertEquals(DieLabels.D20, stored.dieLabel)
        assertEquals(listOf(17), stored.values)
        assertEquals(17, stored.total)
    }

    @Test
    fun `a settled two dice roll is persisted as one record holding both values`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, applicationScope)

        viewModel.onRollSettled(outcome(dieLabel = DieLabels.DOUBLE_D6, values = listOf(4, 6), total = 10))

        val stored = repository.rolls.first().single()
        assertEquals(DieLabels.DOUBLE_D6, stored.dieLabel)
        assertEquals(listOf(4, 6), stored.values)
        assertEquals(10, stored.total)
    }

    @Test
    fun `a percentile roll keeps a scored total that differs from the sum of its dice`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, applicationScope)

        viewModel.onRollSettled(outcome(dieLabel = DieLabels.D100, values = listOf(0, 0), total = 100))

        val stored = repository.rolls.first().single()
        assertEquals(listOf(0, 0), stored.values)
        assertEquals(100, stored.total)
    }

    @Test
    fun `a settled roll is stamped with the time the roll started, not the time it settled`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, applicationScope)

        viewModel.onRollSettled(
            outcome(dieLabel = DieLabels.D6, values = listOf(3), total = 3)
        )

        assertEquals(ROLL_STARTED_MILLIS, repository.rolls.first().single().timestampMillis)
    }

    @Test
    fun `each settled roll appends another record`() = runTest {
        val repository = FakeRollHistoryRepository()
        val viewModel = DiceViewModel(repository, applicationScope)

        viewModel.onRollSettled(outcome(dieLabel = DieLabels.D4, values = listOf(1), total = 1))
        viewModel.onRollSettled(outcome(dieLabel = DieLabels.D4, values = listOf(2), total = 2))

        assertEquals(listOf(1, 2), repository.rolls.first().map { it.total })
    }

    @Test
    fun `a record already in flight completes after the dice ViewModel scope is cancelled`() = runTest {
        val repository = FakeRollHistoryRepository()
        val gate = CompletableDeferred<Unit>()
        repository.recordGate = gate
        val viewModel = DiceViewModel(repository, applicationScope)

        viewModel.onRollSettled(outcome(dieLabel = DieLabels.D6, values = listOf(5), total = 5))
        viewModel.viewModelScope.cancel()
        gate.complete(Unit)

        assertEquals(listOf(5), repository.rolls.first().map { it.total })
    }

    @Test
    fun `clearing the history while a record is in flight keeps the record being written`() = runTest {
        val repository = FakeRollHistoryRepository()
        repository.record(
            RollRecord(dieLabel = DieLabels.D6, values = listOf(1), total = 1, timestampMillis = 1L)
        )
        val gate = CompletableDeferred<Unit>()
        repository.recordGate = gate
        val diceViewModel = DiceViewModel(repository, applicationScope)
        val settingsViewModel = SettingsViewModel(
            FakeSettingsRepository(),
            repository,
            applicationScope
        )

        diceViewModel.onRollSettled(outcome(dieLabel = DieLabels.D20, values = listOf(20), total = 20))
        settingsViewModel.clearRollHistory()
        gate.complete(Unit)

        assertEquals(listOf(20), repository.rolls.first().map { it.total })
    }

    private fun outcome(dieLabel: String, values: List<Int>, total: Int): RollOutcome =
        RollOutcome(
            dieLabel = dieLabel,
            values = values,
            total = total,
            startedAtMillis = ROLL_STARTED_MILLIS
        )

    private companion object {
        const val ROLL_STARTED_MILLIS = 1_786_999_000_000L
    }
}
