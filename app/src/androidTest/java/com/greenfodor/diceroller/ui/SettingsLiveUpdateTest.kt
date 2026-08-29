package com.greenfodor.diceroller.ui

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.FakeSettingsRepository
import com.greenfodor.diceroller.HiltTestActivity
import com.greenfodor.diceroller.R
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
import com.greenfodor.diceroller.feature.settings.presentation.R as SettingsR

private class EmptyRollHistoryRepository : RollHistoryRepository {
    override val rolls = MutableStateFlow(emptyList<RollRecord>())

    override suspend fun record(record: RollRecord) = Unit

    override suspend fun clear() = Unit
}

/**
 * Drives the real [DiceRollerApp] back stack to check that a setting changed on screen is
 * rendered straight away.
 */
@HiltAndroidTest
@UninstallModules(DataModule::class)
class SettingsLiveUpdateTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @BindValue
    val repository: RollHistoryRepository = EmptyRollHistoryRepository()

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
    fun choosingTheNumbersD6FaceStyleMarksItSelectedWhileStillOnTheSettingsScreen() {
        setContent()
        openSettings()

        composeTestRule.onNodeWithText(string(SettingsR.string.d6_face_style_numbers)).performClick()

        composeTestRule.onNodeWithText(string(SettingsR.string.d6_face_style_numbers)).assertIsSelected()
    }

    @Test
    fun choosingTheDarkThemeMarksItSelectedWhileStillOnTheSettingsScreen() {
        setContent()
        openSettings()

        composeTestRule.onNodeWithText(string(SettingsR.string.theme_dark)).performClick()

        composeTestRule.onNodeWithText(string(SettingsR.string.theme_dark)).assertIsSelected()
    }

    @Test
    fun togglingOneColorForAllDiceFlipsItsSwitchWhileStillOnTheDiceColorsScreen() {
        setContent()
        openSettings()
        composeTestRule.onNodeWithText(string(SettingsR.string.settings_dice_colors_label)).performClick()

        val singleColorSwitch = composeTestRule.onNode(
            isToggleable() and hasAnySibling(hasText(string(SettingsR.string.dice_colors_single_label)))
        )

        singleColorSwitch.assertIsOff()
        singleColorSwitch.performClick()
        singleColorSwitch.assertIsOn()
    }

    private fun setContent() {
        composeTestRule.setContent { DiceRollerApp() }
    }

    private fun openSettings() {
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_settings)).performClick()
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
