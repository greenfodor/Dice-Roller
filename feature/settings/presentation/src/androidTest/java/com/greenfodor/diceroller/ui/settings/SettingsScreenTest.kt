package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(
        @StringRes resId: Int
    ): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    @Test
    fun selectingDark_invokesCallbackWithDarkMode() {
        var selected: ThemeMode? = null
        composeTestRule.setContent {
            DiceRollerTheme {
                SettingsScreen(
                    themeMode = ThemeMode.FOLLOW_SYSTEM,
                    onThemeModeSelected = { selected = it },
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText(string(R.string.theme_dark)).performClick()

        assertEquals(ThemeMode.DARK, selected)
    }
}
