package com.greenfodor.diceroller.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Holds the persisted [ThemeMode] for the UI. The first emission is `null` ("not loaded yet"),
 * which the root composable uses to keep the splash screen up and avoid a theme flash.
 */
class SettingsViewModel(
    private val repository: SettingsRepository
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

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setHapticFeedbackEnabled(enabled) }
    }

    fun setShakeToRollEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setShakeToRollEnabled(enabled) }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
        private const val TOGGLE_DEFAULT = true

        fun provideFactory(repository: SettingsRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { SettingsViewModel(repository) }
            }
    }
}
