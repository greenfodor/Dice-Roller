package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.data.DiceColorSettings
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiceColorsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(
        @StringRes resId: Int
    ): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private fun setContent(
        settings: DiceColorSettings = DiceColorSettings(),
        themeMode: ThemeMode = ThemeMode.LIGHT,
        onUseSingleColorToggled: (Boolean) -> Unit = {},
        onSingleColorSelected: (DiceColorOption) -> Unit = {},
        onDiceColorSelected: (DieColorTarget, DiceColorOption) -> Unit = { _, _ -> },
        onRestoreDefaults: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            DiceRollerTheme {
                DiceColorsScreen(
                    settings = settings,
                    themeMode = themeMode,
                    onUseSingleColorToggled = onUseSingleColorToggled,
                    onSingleColorSelected = onSingleColorSelected,
                    onDiceColorSelected = onDiceColorSelected,
                    onRestoreDefaults = onRestoreDefaults,
                    onBack = {}
                )
            }
        }
    }

    @Test
    fun togglingSingleColor_invokesCallbackWithNewValue() {
        var toggled: Boolean? = null
        setContent(
            settings = DiceColorSettings(useSingleColor = false),
            onUseSingleColorToggled = { toggled = it }
        )

        composeTestRule.onAllNodes(isToggleable())[0].performClick()

        assertEquals(true, toggled)
    }

    @Test
    fun singleColorMode_showsAllDiceSection_andHidesPerDieSections() {
        setContent(settings = DiceColorSettings(useSingleColor = true))

        composeTestRule.onNodeWithText(string(R.string.dice_colors_all_dice_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.dice_colors_d4_title)).assertDoesNotExist()
    }

    @Test
    fun perDieMode_showsPerDieSections_andHidesAllDiceSection() {
        setContent(settings = DiceColorSettings(useSingleColor = false))

        composeTestRule.onNodeWithText(string(R.string.dice_colors_d4_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.dice_colors_d6_secondary_title)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.dice_colors_all_dice_title)).assertDoesNotExist()
    }

    @Test
    fun selectingSwatchInSingleMode_invokesSingleColorCallback() {
        var selected: DiceColorOption? = null
        setContent(
            settings = DiceColorSettings(useSingleColor = true, singleColor = DiceColorOption.RED),
            onSingleColorSelected = { selected = it }
        )

        composeTestRule.onNodeWithContentDescription(string(R.string.dice_color_teal)).performClick()

        assertEquals(DiceColorOption.TEAL, selected)
    }

    @Test
    fun selectingSwatchInPerDieMode_invokesDiceColorCallbackForFirstTarget() {
        var target: DieColorTarget? = null
        var option: DiceColorOption? = null
        setContent(
            settings = DiceColorSettings(useSingleColor = false),
            onDiceColorSelected = { t, o ->
                target = t
                option = o
            }
        )

        // D4 is the first target, so its picker holds the first set of swatches.
        composeTestRule.onAllNodesWithContentDescription(string(R.string.dice_color_teal))[0].performClick()

        assertEquals(DieColorTarget.D4, target)
        assertEquals(DiceColorOption.TEAL, option)
    }

    @Test
    fun clickingRestore_showsConfirmationDialog_withoutResetting() {
        var restored = false
        setContent(onRestoreDefaults = { restored = true })

        composeTestRule.onNodeWithText(string(R.string.dice_colors_restore_defaults))
            .performScrollTo().performClick()

        composeTestRule.onNodeWithText(string(R.string.dice_colors_restore_dialog_title)).assertIsDisplayed()
        assertEquals(false, restored)
    }

    @Test
    fun confirmingRestore_invokesCallback() {
        var restored = false
        setContent(onRestoreDefaults = { restored = true })

        composeTestRule.onNodeWithText(string(R.string.dice_colors_restore_defaults))
            .performScrollTo().performClick()
        composeTestRule.onNodeWithText(string(R.string.dice_colors_restore_dialog_confirm)).performClick()

        assertEquals(true, restored)
    }

    @Test
    fun cancelingRestore_dismissesWithoutCallback() {
        var restored = false
        setContent(onRestoreDefaults = { restored = true })

        composeTestRule.onNodeWithText(string(R.string.dice_colors_restore_defaults))
            .performScrollTo().performClick()
        composeTestRule.onNodeWithText(string(R.string.dice_colors_restore_dialog_cancel)).performClick()

        composeTestRule.onNodeWithText(string(R.string.dice_colors_restore_dialog_title)).assertDoesNotExist()
        assertEquals(false, restored)
    }
}
