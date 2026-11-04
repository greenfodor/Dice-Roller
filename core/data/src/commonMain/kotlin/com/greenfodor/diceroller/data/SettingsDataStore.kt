package com.greenfodor.diceroller.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/** Name of the settings Preferences DataStore. */
const val SETTINGS_DATASTORE_NAME = "settings"

/** File name of the settings Preferences DataStore. */
const val SETTINGS_DATASTORE_FILE_NAME = "$SETTINGS_DATASTORE_NAME.preferences_pb"

/** Creates the settings Preferences [DataStore] stored at the path returned by [producePath]. */
fun createSettingsDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
