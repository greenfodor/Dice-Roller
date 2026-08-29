package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.dice.d6.D6
import com.greenfodor.diceroller.ui.dice.rememberDieState
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
    fun theReportedOutcomeIsStampedWhenTheRollStartedNotWhenItSettled() {
        setContent { D6Screen(onRollSettled = { settled += it }) }

        val beforeClick = System.currentTimeMillis()
        composeTestRule.onNodeWithText(string(R.string.roll_button_single)).performClick()
        composeTestRule.mainClock.advanceTimeByFrame()
        val afterClick = System.currentTimeMillis()
        composeTestRule.mainClock.advanceTimeBy(DiceConstants.ROLL_DURATION_MILLIS + ROLL_SETTLE_BUFFER_MILLIS)
        composeTestRule.mainClock.advanceTimeByFrame()

        assertEquals(1, settled.size)
        assertTrue(settled.single().startedAtMillis in beforeClick..afterClick)
    }

    @Test
    fun aSettledRollIsReportedOnceAndNotAgainAfterAStateRestore() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent {
            DiceRollerTheme { D6Screen(onRollSettled = { settled += it }) }
        }

        composeTestRule.onNodeWithText(string(R.string.roll_button_single)).performClick()
        composeTestRule.waitUntil(timeoutMillis = SETTLE_TIMEOUT_MILLIS) { settled.size == 1 }
        restorationTester.emulateSavedInstanceStateRestore()
        composeTestRule.waitForIdle()

        assertEquals(1, settled.size)
    }

    @Test
    fun aRollInterruptedByAStateRestoreIsNotReported() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent {
            DiceRollerTheme { StillDiceScreen(onRollSettled = { settled += it }) }
        }

        composeTestRule.onNodeWithText(string(R.string.roll_button_single)).performClick()
        restorationTester.emulateSavedInstanceStateRestore()
        composeTestRule.waitForIdle()

        assertEquals(0, settled.size)
    }

    @Test
    fun aRollInterruptedBySwitchingDieTypeIsNotReported() {
        var showD6 by mutableStateOf(true)
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            DiceRollerTheme {
                if (showD6) {
                    D6Screen(onRollSettled = { settled += it })
                } else {
                    D20Screen(onRollSettled = { settled += it })
                }
            }
        }

        composeTestRule.onNodeWithText(string(R.string.roll_button_single)).performClick()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.runOnIdle { showD6 = false }
        composeTestRule.mainClock.advanceTimeBy(DiceConstants.ROLL_DURATION_MILLIS + ROLL_SETTLE_BUFFER_MILLIS)
        composeTestRule.mainClock.advanceTimeByFrame()

        assertEquals(0, settled.size)
    }

    @Test
    fun theReportedOutcomeMatchesTheValueShownOnScreen() {
        setContent { D20Screen(onRollSettled = { settled += it }) }

        rollAndSettle(R.string.roll_button_single)

        composeTestRule.onNodeWithText(settled.single().total.toString()).assertExists()
    }

    /** A [DiceScreen] whose die is never drawn, so a started roll stays in flight until disposed. */
    @Composable
    private fun StillDiceScreen(onRollSettled: (RollOutcome) -> Unit) {
        DiceScreen(
            dieStates = listOf(rememberDieState(die = D6)),
            dieLabel = DieLabels.D6,
            rollButtonResId = R.string.roll_button_single,
            onRollSettled = onRollSettled
        ) { }
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
        const val SETTLE_TIMEOUT_MILLIS = 10_000L
        const val PERCENTILE_MAX = 100
    }
}
