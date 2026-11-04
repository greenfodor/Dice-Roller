package com.greenfodor.diceroller.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

private val settingsDataStoreLock = Any()

@Volatile
private var settingsDataStoreInstance: DataStore<Preferences>? = null

/**
 * The process-wide settings [DataStore], stored in `filesDir/datastore/settings.preferences_pb`.
 * Every call returns the same instance.
 */
internal fun settingsDataStore(context: Context): DataStore<Preferences> =
    settingsDataStoreInstance ?: synchronized(settingsDataStoreLock) {
        settingsDataStoreInstance ?: createSettingsDataStore {
            context.applicationContext.preferencesDataStoreFile(SETTINGS_DATASTORE_NAME).absolutePath
        }.also { settingsDataStoreInstance = it }
    }
