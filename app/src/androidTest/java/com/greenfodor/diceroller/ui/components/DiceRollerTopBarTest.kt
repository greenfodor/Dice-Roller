package com.greenfodor.diceroller.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiceRollerTopBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theHistoryButtonIsShownAlongsideTheSettingsButton() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_settings)).assertIsDisplayed()
    }

    @Test
    fun clickingTheHistoryButtonInvokesOnOpenHistoryAndNotOnOpenSettings() {
        var openedHistory = false
        var openedSettings = false
        setContent(
            onOpenHistory = { openedHistory = true },
            onOpenSettings = { openedSettings = true }
        )

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        assertEquals(true, openedHistory)
        assertEquals(false, openedSettings)
    }

    @Test
    fun clickingTheSettingsButtonInvokesOnOpenSettingsAndNotOnOpenHistory() {
        var openedHistory = false
        var openedSettings = false
        setContent(
            onOpenHistory = { openedHistory = true },
            onOpenSettings = { openedSettings = true }
        )

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_settings)).performClick()

        assertEquals(true, openedSettings)
        assertEquals(false, openedHistory)
    }

    private fun setContent(
        onOpenHistory: () -> Unit = {},
        onOpenSettings: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            DiceRollerTheme {
                DiceRollerTopBar(
                    onOpenHistory = onOpenHistory,
                    onOpenSettings = onOpenSettings
                )
            }
        }
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
