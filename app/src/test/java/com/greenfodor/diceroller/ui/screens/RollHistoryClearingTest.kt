package com.greenfodor.diceroller.ui.screens

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
    override val selectedDiceType = MutableStateFlow("SINGLE_D6")

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

    override suspend fun setSelectedDiceType(key: String) {
        selectedDiceType.value = key
    }
}

/** Checks a [DiceViewModel] record against a [SettingsViewModel] clearing the same history. */
@OptIn(ExperimentalCoroutinesApi::class)
class RollHistoryClearingTest {
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
