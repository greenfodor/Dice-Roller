package com.greenfodor.diceroller.ui.settings

import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.data.DiceColorSettings
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.ThemeMode
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

private class FakeSettingsRepository(
    initial: ThemeMode,
    initialHaptics: Boolean = true,
    initialShake: Boolean = true,
    initialFaceStyle: D6FaceStyle = D6FaceStyle.PIPS,
    initialDiceColors: DiceColorSettings = DiceColorSettings()
) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    override val themeMode = state

    private val haptics = MutableStateFlow(initialHaptics)
    override val hapticFeedbackEnabled = haptics

    private val shake = MutableStateFlow(initialShake)
    override val shakeToRollEnabled = shake

    private val faceStyle = MutableStateFlow(initialFaceStyle)
    override val d6FaceStyle = faceStyle

    private val diceColors = MutableStateFlow(initialDiceColors)
    override val diceColorSettings = diceColors

    override suspend fun setThemeMode(mode: ThemeMode) {
        state.update { mode }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        haptics.update { enabled }
    }

    override suspend fun setShakeToRollEnabled(enabled: Boolean) {
        shake.update { enabled }
    }

    override suspend fun setD6FaceStyle(style: D6FaceStyle) {
        faceStyle.update { style }
    }

    override suspend fun setUseSingleDiceColor(enabled: Boolean) {
        diceColors.update { it.copy(useSingleColor = enabled) }
    }

    override suspend fun setSingleDiceColor(option: DiceColorOption) {
        diceColors.update { it.copy(singleColor = option) }
    }

    override suspend fun setDiceColor(target: DieColorTarget, option: DiceColorOption) {
        diceColors.update { it.copy(perDie = it.perDie + (target to option)) }
    }

    override suspend fun resetDiceColors() {
        diceColors.update { DiceColorSettings() }
    }
}

private class FakeRollHistoryRepository : RollHistoryRepository {
    private val state = MutableStateFlow(emptyList<RollRecord>())
    override val rolls = state

    var clearCount = 0
        private set

    override suspend fun record(record: RollRecord) {
        state.update { it + record }
    }

    override suspend fun clear() {
        clearCount++
        state.value = emptyList()
    }
}

private fun settingsViewModel(
    repository: SettingsRepository,
    rollHistoryRepository: RollHistoryRepository = FakeRollHistoryRepository()
): SettingsViewModel = SettingsViewModel(repository, rollHistoryRepository)

class SettingsViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `themeMode starts null then reflects the repository`() = runTest {
        val viewModel = settingsViewModel(FakeSettingsRepository(ThemeMode.DARK))

        assertNull(viewModel.themeMode.value)
        assertEquals(ThemeMode.DARK, viewModel.themeMode.first { it != null })
    }

    @Test
    fun `setThemeMode forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = settingsViewModel(repository)

        viewModel.setThemeMode(ThemeMode.LIGHT)

        assertEquals(ThemeMode.LIGHT, repository.themeMode.first())
    }

    @Test
    fun `hapticFeedbackEnabled reflects the repository`() = runTest {
        val viewModel = settingsViewModel(FakeSettingsRepository(ThemeMode.DARK, initialHaptics = false))

        assertEquals(false, viewModel.hapticFeedbackEnabled.first())
    }

    @Test
    fun `setHapticFeedbackEnabled forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = settingsViewModel(repository)

        viewModel.setHapticFeedbackEnabled(false)

        assertEquals(false, repository.hapticFeedbackEnabled.first())
    }

    @Test
    fun `shakeToRollEnabled reflects the repository`() = runTest {
        val viewModel = settingsViewModel(FakeSettingsRepository(ThemeMode.DARK, initialShake = false))

        assertEquals(false, viewModel.shakeToRollEnabled.first())
    }

    @Test
    fun `setShakeToRollEnabled forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = settingsViewModel(repository)

        viewModel.setShakeToRollEnabled(false)

        assertEquals(false, repository.shakeToRollEnabled.first())
    }

    @Test
    fun `d6FaceStyle reflects the repository`() = runTest {
        val viewModel =
            settingsViewModel(FakeSettingsRepository(ThemeMode.DARK, initialFaceStyle = D6FaceStyle.NUMBERS))

        assertEquals(D6FaceStyle.NUMBERS, viewModel.d6FaceStyle.first())
    }

    @Test
    fun `setD6FaceStyle forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = settingsViewModel(repository)

        viewModel.setD6FaceStyle(D6FaceStyle.NUMBERS)

        assertEquals(D6FaceStyle.NUMBERS, repository.d6FaceStyle.first())
    }

    @Test
    fun `diceColorSettings reflects the repository`() = runTest {
        val viewModel = settingsViewModel(
            FakeSettingsRepository(
                ThemeMode.DARK,
                initialDiceColors = DiceColorSettings(useSingleColor = true, singleColor = DiceColorOption.BLUE)
            )
        )

        val settings = viewModel.diceColorSettings.first { it.useSingleColor }
        assertEquals(DiceColorOption.BLUE, settings.singleColor)
    }

    @Test
    fun `setUseSingleDiceColor forwards the toggle to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = settingsViewModel(repository)

        viewModel.setUseSingleDiceColor(true)

        assertEquals(true, repository.diceColorSettings.first().useSingleColor)
    }

    @Test
    fun `setSingleDiceColor forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = settingsViewModel(repository)

        viewModel.setSingleDiceColor(DiceColorOption.GREEN)

        assertEquals(DiceColorOption.GREEN, repository.diceColorSettings.first().singleColor)
    }

    @Test
    fun `setDiceColor forwards a per-die override to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = settingsViewModel(repository)

        viewModel.setDiceColor(DieColorTarget.D20, DiceColorOption.TEAL)

        assertEquals(
            DiceColorOption.TEAL,
            repository.diceColorSettings.first().optionFor(DieColorTarget.D20)
        )
    }

    @Test
    fun `clearRollHistory clears the roll history repository`() = runTest {
        val rollHistoryRepository = FakeRollHistoryRepository()
        rollHistoryRepository.record(
            RollRecord(dieLabel = DieLabels.D6, values = listOf(4), total = 4, timestampMillis = 1_000L)
        )
        val viewModel = settingsViewModel(FakeSettingsRepository(ThemeMode.DARK), rollHistoryRepository)

        viewModel.clearRollHistory()

        assertEquals(1, rollHistoryRepository.clearCount)
        assertEquals(emptyList<RollRecord>(), rollHistoryRepository.rolls.first())
    }

    @Test
    fun `clearRollHistory leaves the persisted settings untouched`() = runTest {
        val settingsRepository = FakeSettingsRepository(ThemeMode.DARK)
        val viewModel = settingsViewModel(settingsRepository, FakeRollHistoryRepository())

        viewModel.clearRollHistory()

        assertEquals(ThemeMode.DARK, settingsRepository.themeMode.first())
        assertEquals(DiceColorSettings(), settingsRepository.diceColorSettings.first())
    }

    @Test
    fun `resetDiceColors restores the defaults in the repository`() = runTest {
        val repository = FakeSettingsRepository(
            ThemeMode.FOLLOW_SYSTEM,
            initialDiceColors = DiceColorSettings(useSingleColor = true, singleColor = DiceColorOption.PINK)
        )
        val viewModel = settingsViewModel(repository)

        viewModel.resetDiceColors()

        assertEquals(DiceColorSettings(), repository.diceColorSettings.first())
    }
}
