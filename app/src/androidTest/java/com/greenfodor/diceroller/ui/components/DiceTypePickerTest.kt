package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiceTypePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theSheetContentIsHiddenWhileTheSheetIsCollapsed() {
        setContent()

        handle().assertIsDisplayed()
        assertSheetCollapsed()
    }

    @Test
    fun clickingTheCollapsedHandleExpandsTheSheet() {
        setContent()

        handle().performClick()
        composeTestRule.waitForIdle()

        assertSheetExpanded()
    }

    @Test
    fun clickingTheHandleWhileExpandedCollapsesTheSheet() {
        setContent()

        expandSheet()
        handle().performClick()
        composeTestRule.waitForIdle()

        assertSheetCollapsed()
    }

    @Test
    fun aDragShorterThanTheThresholdSettlesBackWhileAFullDragExpands() {
        setContent()

        handle().performTouchInput {
            swipeUp(startY = centerY, endY = centerY - SHORT_DRAG_PX, durationMillis = SLOW_DRAG_MILLIS)
        }
        composeTestRule.waitForIdle()
        assertSheetCollapsed()

        val travel = composeTestRule.onRoot().fetchSemanticsNode().size.height * FULL_DRAG_FRACTION
        handle().performTouchInput {
            swipeUp(startY = centerY, endY = centerY - travel, durationMillis = SLOW_DRAG_MILLIS)
        }
        composeTestRule.waitForIdle()
        assertSheetExpanded()
    }

    @Test
    fun theSheetShowsOneTileForEveryDiceTypeEntry() {
        setContent()

        expandSheet()

        DiceType.entries.forEach { diceType ->
            tile(diceType).assertIsDisplayed()
        }
    }

    @Test
    fun everySheetTileIsSquare() {
        setContent()

        expandSheet()

        DiceType.entries.forEach { diceType ->
            val tile = tile(diceType).fetchSemanticsNode().size
            assertEquals("$diceType tile", tile.width.toFloat(), tile.height.toFloat(), TOLERANCE_PX)
        }
    }

    @Test
    fun tappingATileReportsThatDiceType() {
        var reported: DiceType? = null
        setContent(onDiceTypeSelected = { reported = it })

        expandSheet()
        tile(DiceType.SINGLE_D20).performClick()

        assertEquals(DiceType.SINGLE_D20, reported)
    }

    @Test
    fun theSheetCollapsesAfterATileIsTapped() {
        setContent()

        expandSheet()
        tile(DiceType.SINGLE_D20).performClick()
        composeTestRule.waitForIdle()

        assertSheetCollapsed()
    }

    @Test
    fun theTileForTheActiveDiceTypeIsSelectedAndTheOthersAreNot() {
        setContent(initialDiceType = DiceType.SINGLE_D8)

        expandSheet()

        tile(DiceType.SINGLE_D8).assertIsSelected()
        tile(DiceType.SINGLE_D6).assertIsNotSelected()
    }

    private fun expandSheet() {
        handle().performClick()
        composeTestRule.waitForIdle()
        assertSheetExpanded()
    }

    private fun assertSheetExpanded() {
        composeTestRule.onNodeWithText(string(R.string.dice_type_picker_title)).assertIsDisplayed()
    }

    private fun assertSheetCollapsed() {
        composeTestRule.onNodeWithText(string(R.string.dice_type_picker_title)).assertIsNotDisplayed()
    }

    private fun handle() =
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type))

    private fun tile(diceType: DiceType) =
        composeTestRule.onNode(hasText(string(diceType.labelResId)) and isSelectable())

    private fun setContent(
        initialDiceType: DiceType = DiceType.SINGLE_D6,
        onDiceTypeSelected: (DiceType) -> Unit = {}
    ) {
        composeTestRule.setContent {
            DiceRollerTheme {
                var selectedDiceType by remember { mutableStateOf(initialDiceType) }

                DiceTypePickerSheet(
                    selectedDiceType = selectedDiceType,
                    onDiceTypeSelected = { diceType ->
                        selectedDiceType = diceType
                        onDiceTypeSelected(diceType)
                    },
                    content = { Box(modifier = Modifier.fillMaxSize()) }
                )
            }
        }
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private companion object {
        const val TOLERANCE_PX = 1f
        const val SHORT_DRAG_PX = 60f
        const val FULL_DRAG_FRACTION = 0.8f
        const val SLOW_DRAG_MILLIS = 1_000L
    }
}
