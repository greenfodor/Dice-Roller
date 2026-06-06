package com.greenfodor.diceroller.ui.screens

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class D6ScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val rollLabel: String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.roll_button_single)

    @Test
    fun rollButton_isDisabledWhileRolling_thenReEnables() {
        // Drive the animation clock manually so the rolling window is observable.
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            DiceRollerTheme {
                D6Screen()
            }
        }

        composeTestRule.onNodeWithText(rollLabel).assertIsEnabled()

        composeTestRule.onNodeWithText(rollLabel).performClick()
        composeTestRule.mainClock.advanceTimeByFrame()

        // The roll has started but not finished: the button must be disabled.
        composeTestRule.onNodeWithText(rollLabel).assertIsNotEnabled()

        // Advance well past the roll duration so the animation settles.
        composeTestRule.mainClock.advanceTimeBy(DiceConstants.ROLL_DURATION_MILLIS + ROLL_SETTLE_BUFFER_MILLIS)
        composeTestRule.mainClock.advanceTimeByFrame()

        composeTestRule.onNodeWithText(rollLabel).assertIsEnabled()
    }

    private companion object {
        const val ROLL_SETTLE_BUFFER_MILLIS = 500L
    }
}
