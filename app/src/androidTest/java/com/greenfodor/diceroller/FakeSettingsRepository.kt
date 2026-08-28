package com.greenfodor.diceroller

import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.data.DiceColorSettings
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** In-memory [SettingsRepository] so instrumentation tests never touch the real DataStore. */
class FakeSettingsRepository : SettingsRepository {
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
