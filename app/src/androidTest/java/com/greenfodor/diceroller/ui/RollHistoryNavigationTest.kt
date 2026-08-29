package com.greenfodor.diceroller.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.FakeSettingsRepository
import com.greenfodor.diceroller.HiltTestActivity
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.di.DataModule
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.ZoneId
import com.greenfodor.diceroller.feature.history.presentation.R as HistoryR
import com.greenfodor.diceroller.feature.settings.presentation.R as SettingsR

private class FakeRollHistoryRepository : RollHistoryRepository {
    val state = MutableStateFlow(listOf(sampleRoll))
    override val rolls = state

    override suspend fun record(record: RollRecord) {
        state.value = state.value + record
    }

    override suspend fun clear() {
        state.value = emptyList()
    }
}

private val sampleRoll = RollRecord(
    id = 1,
    dieLabel = DieLabels.DOUBLE_D6,
    values = listOf(4, 6),
    total = 10,
    timestampMillis = 1_787_000_000_000L
)

/** Drives the real [DiceRollerApp] back stack, with only the persistence layer faked. */
@HiltAndroidTest
@UninstallModules(DataModule::class)
class RollHistoryNavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @BindValue
    val repository: RollHistoryRepository = FakeRollHistoryRepository()

    @BindValue
    val settingsRepository: SettingsRepository = FakeSettingsRepository()

    @BindValue
    val clock: Clock = Clock.systemDefaultZone()

    @BindValue
    val zoneId: ZoneId = ZoneId.systemDefault()

    @BindValue
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Before
    fun inject() {
        hiltRule.inject()
    }

    @Test
    fun theHistoryIsNotShownUntilTheTopBarButtonIsClicked() {
        setContent()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_title)).assertDoesNotExist()
    }

    @Test
    fun clickingTheTopBarHistoryButtonOpensTheHistoryOverTheDiceScreen() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(DieLabels.DOUBLE_D6).assertIsDisplayed()
    }

    @Test
    fun theHistoryEntryResolvesItsViewModelThroughHilt() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(sampleRoll.total.toString()).assertIsDisplayed()
    }

    @Test
    fun closingTheHistoryReturnsToTheDiceScreen() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()
        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_close)).performClick()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_title)).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).assertIsDisplayed()
    }

    @Test
    fun anEmptyHistoryOpensOnTheNoRollsMessage() {
        (repository as FakeRollHistoryRepository).state.value = emptyList()
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_empty)).assertIsDisplayed()
    }

    @Test
    fun theSettingsEntryShowsItsSettingsAsSoonAsItIsOpened() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_settings)).performClick()

        composeTestRule.onNodeWithText(string(SettingsR.string.settings_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(SettingsR.string.settings_theme_title)).assertIsDisplayed()
    }

    private fun setContent() {
        composeTestRule.setContent { DiceRollerApp() }
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
