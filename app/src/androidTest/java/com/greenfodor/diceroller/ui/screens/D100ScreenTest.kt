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
import com.greenfodor.diceroller.ui.dice.d100.PercentileTensDie
import com.greenfodor.diceroller.ui.dice.d100.PercentileUnitsDie
import com.greenfodor.diceroller.ui.dice.d100.percentileValue
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class D100ScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val rollLabel: String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.roll_button_multiple)

    @Test
    fun rollButton_isDisabledWhileRolling_thenReEnables() {
        // Two dice roll together on this screen; the shared roll flow must still gate the button.
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            DiceRollerTheme {
                D100Screen()
            }
        }

        composeTestRule.onNodeWithText(rollLabel).assertIsEnabled()

        composeTestRule.onNodeWithText(rollLabel).performClick()
        composeTestRule.mainClock.advanceTimeByFrame()

        composeTestRule.onNodeWithText(rollLabel).assertIsNotEnabled()

        composeTestRule.mainClock.advanceTimeBy(DiceConstants.ROLL_DURATION_MILLIS + ROLL_SETTLE_BUFFER_MILLIS)
        composeTestRule.mainClock.advanceTimeByFrame()

        composeTestRule.onNodeWithText(rollLabel).assertIsEnabled()
    }

    @Test
    fun resultText_showsInitialPercentileTotal_atRest() {
        // At rest the shared result text should show the combined percentile value of the two
        // dice's starting faces (the only Text nodes are this result and the roll button).
        val initial = percentileValue(
            PercentileTensDie.faces.first().value + PercentileUnitsDie.faces.first().value
        )
        composeTestRule.setContent {
            DiceRollerTheme {
                D100Screen()
            }
        }

        composeTestRule.onNodeWithText(initial.toString()).assertExists()
    }

    private companion object {
        const val ROLL_SETTLE_BUFFER_MILLIS = 500L
    }
}
