package com.greenfodor.diceroller.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/**
 * [SettingsRepository] backed by a Preferences [DataStore]. Read failures (corrupt file / IO)
 * degrade gracefully to empty preferences so the app always resolves a usable [ThemeMode].
 *
 * Takes the [DataStore] directly so it can be unit-tested over a temp file; production code
 * wires it from a [Context] via [create].
 */
class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override val themeMode: Flow<ThemeMode> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> ThemeMode.fromName(preferences[THEME_MODE_KEY]) }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

        fun create(context: Context): DataStoreSettingsRepository =
            DataStoreSettingsRepository(context.settingsDataStore)
    }
}
