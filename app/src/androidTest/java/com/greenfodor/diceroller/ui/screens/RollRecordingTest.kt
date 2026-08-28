package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RollRecordingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val settled = mutableListOf<RollOutcome>()

    @Test
    fun rollingASingleD6ReportsOneOutcomeLabelledD6() {
        setContent { D6Screen(onRollSettled = { settled += it }) }

        rollAndSettle(R.string.roll_button_single)

        assertEquals(1, settled.size)
        assertEquals(DieLabels.D6, settled.single().dieLabel)
        assertEquals(1, settled.single().values.size)
    }

    @Test
    fun rollingTwoD6ReportsASingleOutcomeHoldingBothValues() {
        setContent { DoubleD6Screen(onRollSettled = { settled += it }) }

        rollAndSettle(R.string.roll_button_multiple)

        assertEquals(1, settled.size)
        val outcome = settled.single()
        assertEquals(DieLabels.DOUBLE_D6, outcome.dieLabel)
        assertEquals(2, outcome.values.size)
        assertEquals(outcome.values.sum(), outcome.total)
    }

    @Test
    fun rollingAPercentileD100ReportsASingleOutcomeHoldingBothDice() {
        setContent { D100Screen(onRollSettled = { settled += it }) }

        rollAndSettle(R.string.roll_button_multiple)

        assertEquals(1, settled.size)
        val outcome = settled.single()
        assertEquals(DieLabels.D100, outcome.dieLabel)
        assertEquals(2, outcome.values.size)
        assertTrue(outcome.total in 1..PERCENTILE_MAX)
    }

    @Test
    fun rollingTwiceReportsTwoOutcomes() {
        setContent { D20Screen(onRollSettled = { settled += it }) }

        rollAndSettle(R.string.roll_button_single)
        rollAndSettle(R.string.roll_button_single)

        assertEquals(2, settled.size)
        assertTrue(settled.all { it.dieLabel == DieLabels.D20 })
    }

    @Test
    fun nothingIsReportedWhileTheDiceAreStillRolling() {
        composeTestRule.mainClock.autoAdvance = false
        setContent { D6Screen(onRollSettled = { settled += it }) }

        composeTestRule.onNodeWithText(string(R.string.roll_button_single)).performClick()
        composeTestRule.mainClock.advanceTimeByFrame()

        assertEquals(0, settled.size)
    }

    @Test
    fun theReportedOutcomeMatchesTheValueShownOnScreen() {
        setContent { D20Screen(onRollSettled = { settled += it }) }

        rollAndSettle(R.string.roll_button_single)

        composeTestRule.onNodeWithText(settled.single().total.toString()).assertExists()
    }

    private fun setContent(screen: @Composable () -> Unit) {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            DiceRollerTheme { screen() }
        }
    }

    private fun rollAndSettle(rollButtonResId: Int) {
        composeTestRule.onNodeWithText(string(rollButtonResId)).performClick()
        composeTestRule.mainClock.advanceTimeBy(DiceConstants.ROLL_DURATION_MILLIS + ROLL_SETTLE_BUFFER_MILLIS)
        composeTestRule.mainClock.advanceTimeByFrame()
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private companion object {
        const val ROLL_SETTLE_BUFFER_MILLIS = 500L
        const val PERCENTILE_MAX = 100
    }
}
