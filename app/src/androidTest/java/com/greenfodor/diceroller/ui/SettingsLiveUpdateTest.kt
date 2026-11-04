package com.greenfodor.diceroller.ui

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.FakeSettingsRepository
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.fakeDataKoinTestRule
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.d6_face_style_numbers
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_colors_single_label
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.settings_dice_colors_label
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.theme_dark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.junit.Rule
import org.junit.Test

private class EmptyRollHistoryRepository : RollHistoryRepository {
    override val rolls = MutableStateFlow(emptyList<RollRecord>())

    override suspend fun record(record: RollRecord) = Unit

    override suspend fun clear() = Unit
}

/**
 * Drives the real [DiceRollerApp] back stack to check that a setting changed on screen is
 * rendered straight away.
 */
class SettingsLiveUpdateTest {
    @get:Rule(order = 0)
    val koinTestRule = fakeDataKoinTestRule(EmptyRollHistoryRepository(), FakeSettingsRepository())

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun choosingTheNumbersD6FaceStyleMarksItSelectedWhileStillOnTheSettingsScreen() {
        setContent()
        openSettings()

        composeTestRule.onNodeWithText(string(Res.string.d6_face_style_numbers)).performScrollTo().performClick()

        composeTestRule.onNodeWithText(string(Res.string.d6_face_style_numbers)).assertIsSelected()
    }

    @Test
    fun choosingTheDarkThemeMarksItSelectedWhileStillOnTheSettingsScreen() {
        setContent()
        openSettings()

        composeTestRule.onNodeWithText(string(Res.string.theme_dark)).performScrollTo().performClick()

        composeTestRule.onNodeWithText(string(Res.string.theme_dark)).assertIsSelected()
    }

    @Test
    fun togglingOneColorForAllDiceFlipsItsSwitchWhileStillOnTheDiceColorsScreen() {
        setContent()
        openSettings()
        composeTestRule.onNodeWithText(string(Res.string.settings_dice_colors_label))
            .performScrollTo()
            .performClick()

        val singleColorSwitch = composeTestRule.onNode(
            isToggleable() and hasAnySibling(hasText(string(Res.string.dice_colors_single_label)))
        )

        singleColorSwitch.assertIsOff()
        singleColorSwitch.performClick()
        singleColorSwitch.assertIsOn()
    }

    private fun setContent() {
        composeTestRule.setContent { DiceRollerApp(windowSizeClass = CompactWindowSizeClass) }
    }

    private fun openSettings() {
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_settings)).performClick()
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private fun string(resource: StringResource): String = runBlocking { getString(resource) }
}
