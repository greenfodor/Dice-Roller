package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.ui.test.isToggleable
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

    private fun setContent(
        themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
        onThemeModeSelected: (ThemeMode) -> Unit = {},
        hapticFeedbackEnabled: Boolean = true,
        hapticFeedbackSupported: Boolean = true,
        onHapticFeedbackToggled: (Boolean) -> Unit = {},
        shakeToRollEnabled: Boolean = true,
        shakeToRollSupported: Boolean = true,
        onShakeToRollToggled: (Boolean) -> Unit = {}
    ) {
        composeTestRule.setContent {
            DiceRollerTheme {
                SettingsScreen(
                    themeMode = themeMode,
                    onThemeModeSelected = onThemeModeSelected,
                    hapticFeedbackEnabled = hapticFeedbackEnabled,
                    hapticFeedbackSupported = hapticFeedbackSupported,
                    onHapticFeedbackToggled = onHapticFeedbackToggled,
                    shakeToRollEnabled = shakeToRollEnabled,
                    shakeToRollSupported = shakeToRollSupported,
                    onShakeToRollToggled = onShakeToRollToggled,
                    onBack = {}
                )
            }
        }
    }

    @Test
    fun selectingDark_invokesCallbackWithDarkMode() {
        var selected: ThemeMode? = null
        setContent(onThemeModeSelected = { selected = it })

        composeTestRule.onNodeWithText(string(R.string.theme_dark)).performClick()

        assertEquals(ThemeMode.DARK, selected)
    }

    @Test
    fun togglingHaptics_invokesCallbackWithNewValue() {
        var toggled: Boolean? = null
        setContent(hapticFeedbackEnabled = true, onHapticFeedbackToggled = { toggled = it })

        // The haptics switch is the first toggle on the screen.
        composeTestRule.onAllNodes(isToggleable())[0].performClick()

        assertEquals(false, toggled)
    }

    @Test
    fun togglingShake_invokesCallbackWithNewValue() {
        var toggled: Boolean? = null
        setContent(shakeToRollEnabled = true, onShakeToRollToggled = { toggled = it })

        // The shake switch is the second toggle on the screen.
        composeTestRule.onAllNodes(isToggleable())[1].performClick()

        assertEquals(false, toggled)
    }

    @Test
    fun whenHapticsUnsupported_showsUnsupportedMessage() {
        setContent(hapticFeedbackEnabled = false, hapticFeedbackSupported = false)

        composeTestRule.onNodeWithText(string(R.string.settings_unsupported)).assertExists()
    }

    @Test
    fun whenShakeUnsupported_showsUnsupportedMessage() {
        setContent(shakeToRollEnabled = false, shakeToRollSupported = false)

        composeTestRule.onNodeWithText(string(R.string.settings_unsupported)).assertExists()
    }
}
