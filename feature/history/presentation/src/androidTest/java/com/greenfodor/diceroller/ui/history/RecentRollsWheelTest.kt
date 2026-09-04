package com.greenfodor.diceroller.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentRollsWheelTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun everyRecentRollIsShown() {
        setContent(threeRolls())

        composeTestRule.onNodeWithText(OLDEST).assertIsDisplayed()
        composeTestRule.onNodeWithText(MIDDLE).assertIsDisplayed()
        composeTestRule.onNodeWithText(NEWEST).assertIsDisplayed()
    }

    @Test
    fun theNewestRollSitsAtTheBottomOfTheWheel() {
        setContent(threeRolls())

        assertTrue(topOf(NEWEST) > topOf(MIDDLE))
        assertTrue(topOf(MIDDLE) > topOf(OLDEST))
    }

    @Test
    fun anEmptyWheelShowsNoRolls() {
        setContent(emptyList())

        composeTestRule.onNodeWithText(NEWEST).assertDoesNotExist()
    }

    @Test
    fun aNewRollTakesTheBottomSlotAndPushesTheOldestOffTheWheel() {
        var rolls by mutableStateOf(threeRolls())
        composeTestRule.setContent {
            DiceRollerTheme {
                RecentRollsWheel(rolls = rolls)
            }
        }

        composeTestRule.runOnIdle {
            rolls = rolls.drop(1) + RecentRollUiModel(id = 4, dieLabel = "d6", total = "6")
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(ADDED).assertIsDisplayed()
        composeTestRule.onNodeWithText(OLDEST).assertDoesNotExist()
        assertTrue(topOf(ADDED) > topOf(NEWEST))
    }

    private fun setContent(rolls: List<RecentRollUiModel>) {
        composeTestRule.setContent {
            DiceRollerTheme {
                RecentRollsWheel(rolls = rolls)
            }
        }
    }

    private fun threeRolls(): List<RecentRollUiModel> = listOf(
        RecentRollUiModel(id = 1, dieLabel = "d100", total = "100", breakdown = "(00 + 0)"),
        RecentRollUiModel(id = 2, dieLabel = "2d6", total = "7", breakdown = "(3 + 4)"),
        RecentRollUiModel(id = 3, dieLabel = "d20", total = "20")
    )

    private fun topOf(text: String): Float =
        composeTestRule.onNodeWithText(text).fetchSemanticsNode().boundsInRoot.top

    private companion object {
        const val OLDEST = "d100  100  (00 + 0)"
        const val MIDDLE = "2d6  7  (3 + 4)"
        const val NEWEST = "d20  20"
        const val ADDED = "d6  6"
    }
}
