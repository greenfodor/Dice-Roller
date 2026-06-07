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
}
