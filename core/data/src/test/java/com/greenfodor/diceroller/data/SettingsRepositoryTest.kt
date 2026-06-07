package com.greenfodor.diceroller.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SettingsRepositoryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun newRepository(): DataStoreSettingsRepository {
        val dataStore: DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                produceFile = { File(tempFolder.newFolder(), "settings.preferences_pb") }
            )
        return DataStoreSettingsRepository(dataStore)
    }

    @Test
    fun `themeMode defaults to FOLLOW_SYSTEM when nothing is persisted`() = runTest {
        assertEquals(ThemeMode.FOLLOW_SYSTEM, newRepository().themeMode.first())
    }

    @Test
    fun `setThemeMode persists the selected mode`() = runTest {
        val repository = newRepository()

        repository.setThemeMode(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, repository.themeMode.first())
    }
}
