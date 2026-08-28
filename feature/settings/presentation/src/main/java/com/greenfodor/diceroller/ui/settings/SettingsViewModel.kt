package com.greenfodor.diceroller.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.data.DiceColorSettings
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the persisted [ThemeMode] for the UI. The first emission is `null` ("not loaded yet"),
 * which the root composable uses to keep the splash screen up and avoid a theme flash.
 *
 * Also owns clearing the roll history, which is reachable only from the settings screen.
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repository: SettingsRepository,
        private val rollHistoryRepository: RollHistoryRepository
    ) : ViewModel() {
        val themeMode: StateFlow<ThemeMode?> =
            repository.themeMode.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = null
            )

        val hapticFeedbackEnabled: StateFlow<Boolean> =
            repository.hapticFeedbackEnabled.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = TOGGLE_DEFAULT
            )

        val shakeToRollEnabled: StateFlow<Boolean> =
            repository.shakeToRollEnabled.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = TOGGLE_DEFAULT
            )

        val d6FaceStyle: StateFlow<D6FaceStyle> =
            repository.d6FaceStyle.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = D6FaceStyle.PIPS
            )

        val diceColorSettings: StateFlow<DiceColorSettings> =
            repository.diceColorSettings.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = DiceColorSettings()
            )

        fun setThemeMode(mode: ThemeMode) {
            viewModelScope.launch { repository.setThemeMode(mode) }
        }

        fun setHapticFeedbackEnabled(enabled: Boolean) {
            viewModelScope.launch { repository.setHapticFeedbackEnabled(enabled) }
        }

        fun setShakeToRollEnabled(enabled: Boolean) {
            viewModelScope.launch { repository.setShakeToRollEnabled(enabled) }
        }

        fun setD6FaceStyle(style: D6FaceStyle) {
            viewModelScope.launch { repository.setD6FaceStyle(style) }
        }

        fun setUseSingleDiceColor(enabled: Boolean) {
            viewModelScope.launch { repository.setUseSingleDiceColor(enabled) }
        }

        fun setSingleDiceColor(option: DiceColorOption) {
            viewModelScope.launch { repository.setSingleDiceColor(option) }
        }

        fun setDiceColor(target: DieColorTarget, option: DiceColorOption) {
            viewModelScope.launch { repository.setDiceColor(target, option) }
        }

        fun resetDiceColors() {
            viewModelScope.launch { repository.resetDiceColors() }
        }

        /** Deletes every recorded roll. The settings screen is the only entry point for this. */
        fun clearRollHistory() {
            viewModelScope.launch { rollHistoryRepository.clear() }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
            const val TOGGLE_DEFAULT = true
        }
    }
