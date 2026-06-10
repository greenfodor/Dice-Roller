package com.greenfodor.diceroller.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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

    override val hapticFeedbackEnabled: Flow<Boolean> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> preferences[HAPTIC_FEEDBACK_KEY] ?: HAPTIC_FEEDBACK_DEFAULT }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAPTIC_FEEDBACK_KEY] = enabled
        }
    }

    override val shakeToRollEnabled: Flow<Boolean> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> preferences[SHAKE_TO_ROLL_KEY] ?: SHAKE_TO_ROLL_DEFAULT }

    override suspend fun setShakeToRollEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHAKE_TO_ROLL_KEY] = enabled
        }
    }

    override val d6FaceStyle: Flow<D6FaceStyle> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> D6FaceStyle.fromName(preferences[D6_FACE_STYLE_KEY]) }

    override suspend fun setD6FaceStyle(style: D6FaceStyle) {
        dataStore.edit { preferences ->
            preferences[D6_FACE_STYLE_KEY] = style.name
        }
    }

    override val diceColorSettings: Flow<DiceColorSettings> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences ->
                DiceColorSettings(
                    useSingleColor = preferences[DICE_USE_SINGLE_COLOR_KEY] ?: DICE_USE_SINGLE_COLOR_DEFAULT,
                    singleColor = preferences[DICE_SINGLE_COLOR_KEY]
                        ?.let { DiceColorOption.fromName(it) }
                        ?: DiceColorSettings.DEFAULT_SINGLE_COLOR,
                    perDie = DieColorTarget.entries.associateWith { target ->
                        preferences[diceColorKey(target)]
                            ?.let { DiceColorOption.fromName(it) }
                            ?: DiceColorSettings.DEFAULT_PER_DIE.getValue(target)
                    }
                )
            }

    override suspend fun setUseSingleDiceColor(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DICE_USE_SINGLE_COLOR_KEY] = enabled
        }
    }

    override suspend fun setSingleDiceColor(option: DiceColorOption) {
        dataStore.edit { preferences ->
            preferences[DICE_SINGLE_COLOR_KEY] = option.name
        }
    }

    override suspend fun setDiceColor(target: DieColorTarget, option: DiceColorOption) {
        dataStore.edit { preferences ->
            preferences[diceColorKey(target)] = option.name
        }
    }

    override suspend fun resetDiceColors() {
        dataStore.edit { preferences ->
            preferences.remove(DICE_USE_SINGLE_COLOR_KEY)
            preferences.remove(DICE_SINGLE_COLOR_KEY)
            DieColorTarget.entries.forEach { target -> preferences.remove(diceColorKey(target)) }
        }
    }

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val HAPTIC_FEEDBACK_KEY = booleanPreferencesKey("haptic_feedback_enabled")
        private const val HAPTIC_FEEDBACK_DEFAULT = true
        private val SHAKE_TO_ROLL_KEY = booleanPreferencesKey("shake_to_roll_enabled")
        private const val SHAKE_TO_ROLL_DEFAULT = true
        private val D6_FACE_STYLE_KEY = stringPreferencesKey("d6_face_style")
        private val DICE_USE_SINGLE_COLOR_KEY = booleanPreferencesKey("dice_use_single_color")
        private const val DICE_USE_SINGLE_COLOR_DEFAULT = false
        private val DICE_SINGLE_COLOR_KEY = stringPreferencesKey("dice_single_color")

        private fun diceColorKey(target: DieColorTarget) = stringPreferencesKey("dice_color_${target.name}")

        fun create(context: Context): DataStoreSettingsRepository =
            DataStoreSettingsRepository(context.settingsDataStore)
    }
}
