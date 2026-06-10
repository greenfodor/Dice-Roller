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

    @Test
    fun `hapticFeedbackEnabled defaults to true when nothing is persisted`() = runTest {
        assertEquals(true, newRepository().hapticFeedbackEnabled.first())
    }

    @Test
    fun `setHapticFeedbackEnabled persists the selected value`() = runTest {
        val repository = newRepository()

        repository.setHapticFeedbackEnabled(false)

        assertEquals(false, repository.hapticFeedbackEnabled.first())
    }

    @Test
    fun `shakeToRollEnabled defaults to true when nothing is persisted`() = runTest {
        assertEquals(true, newRepository().shakeToRollEnabled.first())
    }

    @Test
    fun `setShakeToRollEnabled persists the selected value`() = runTest {
        val repository = newRepository()

        repository.setShakeToRollEnabled(false)

        assertEquals(false, repository.shakeToRollEnabled.first())
    }

    @Test
    fun `d6FaceStyle defaults to PIPS when nothing is persisted`() = runTest {
        assertEquals(D6FaceStyle.PIPS, newRepository().d6FaceStyle.first())
    }

    @Test
    fun `setD6FaceStyle persists the selected style`() = runTest {
        val repository = newRepository()

        repository.setD6FaceStyle(D6FaceStyle.NUMBERS)

        assertEquals(D6FaceStyle.NUMBERS, repository.d6FaceStyle.first())
    }

    @Test
    fun `diceColorSettings defaults to distinct per-die colors when nothing is persisted`() = runTest {
        assertEquals(DiceColorSettings(), newRepository().diceColorSettings.first())
    }

    @Test
    fun `setUseSingleDiceColor persists the toggle`() = runTest {
        val repository = newRepository()

        repository.setUseSingleDiceColor(true)

        assertEquals(true, repository.diceColorSettings.first().useSingleColor)
    }

    @Test
    fun `setSingleDiceColor persists the single color`() = runTest {
        val repository = newRepository()

        repository.setSingleDiceColor(DiceColorOption.GREEN)

        assertEquals(DiceColorOption.GREEN, repository.diceColorSettings.first().singleColor)
    }

    @Test
    fun `setDiceColor persists a per-die override`() = runTest {
        val repository = newRepository()

        repository.setDiceColor(DieColorTarget.D20, DiceColorOption.TEAL)

        assertEquals(DiceColorOption.TEAL, repository.diceColorSettings.first().optionFor(DieColorTarget.D20))
    }

    @Test
    fun `resetDiceColors clears overrides back to defaults`() = runTest {
        val repository = newRepository()
        repository.setUseSingleDiceColor(true)
        repository.setSingleDiceColor(DiceColorOption.PINK)
        repository.setDiceColor(DieColorTarget.D20, DiceColorOption.TEAL)

        repository.resetDiceColors()

        assertEquals(DiceColorSettings(), repository.diceColorSettings.first())
    }
}
