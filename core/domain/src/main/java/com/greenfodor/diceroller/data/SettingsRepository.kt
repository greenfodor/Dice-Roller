package com.greenfodor.diceroller.data

import kotlinx.coroutines.flow.Flow

/**
 * Persisted app configuration. Implemented by `:core:data`'s DataStore-backed repository;
 * kept as an interface so the presentation layer can be unit-tested with a fake.
 */
interface SettingsRepository {
    /** The persisted theme mode, defaulting to [ThemeMode.FOLLOW_SYSTEM]. */
    val themeMode: Flow<ThemeMode>

    /** Persist the user's theme choice. */
    suspend fun setThemeMode(mode: ThemeMode)

    /** Whether roll haptic feedback is enabled, defaulting to `true`. */
    val hapticFeedbackEnabled: Flow<Boolean>

    /** Persist the user's haptic feedback choice. */
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)

    /** Whether shake-to-roll is enabled, defaulting to `true`. */
    val shakeToRollEnabled: Flow<Boolean>

    /** Persist the user's shake-to-roll choice. */
    suspend fun setShakeToRollEnabled(enabled: Boolean)

    /** How the D6's faces are marked, defaulting to [D6FaceStyle.PIPS]. */
    val d6FaceStyle: Flow<D6FaceStyle>

    /** Persist the user's D6 face-style choice. */
    suspend fun setD6FaceStyle(style: D6FaceStyle)
}
