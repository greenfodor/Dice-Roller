package com.greenfodor.diceroller.ui.components

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
    fun clickingTheFabOpensTheDiceTypePickerSheet() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.dice_type_picker_title)).assertDoesNotExist()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type)).performClick()

        composeTestRule.onNodeWithText(string(R.string.dice_type_picker_title)).assertIsDisplayed()
    }

    @Test
    fun theSheetShowsOneTileForEveryDiceTypeEntry() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type)).performClick()

        DiceType.entries.forEach { diceType ->
            tile(diceType).assertIsDisplayed()
        }
    }

    @Test
    fun tappingATileReportsThatDiceType() {
        var reported: DiceType? = null
        setContent(onDiceTypeSelected = { reported = it })

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type)).performClick()
        tile(DiceType.SINGLE_D20).performClick()

        assertEquals(DiceType.SINGLE_D20, reported)
    }

    @Test
    fun theSheetIsDismissedAfterATileIsTapped() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type)).performClick()
        tile(DiceType.SINGLE_D20).performClick()

        composeTestRule.waitUntil(SHEET_DISMISS_TIMEOUT_MILLIS) {
            composeTestRule
                .onAllNodesWithText(string(R.string.dice_type_picker_title))
                .fetchSemanticsNodes()
                .isEmpty()
        }
    }

    @Test
    fun theTileForTheActiveDiceTypeIsSelectedAndTheOthersAreNot() {
        setContent(initialDiceType = DiceType.SINGLE_D8)

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type)).performClick()

        tile(DiceType.SINGLE_D8).assertIsSelected()
        tile(DiceType.SINGLE_D6).assertIsNotSelected()
    }

    private fun tile(diceType: DiceType) =
        composeTestRule.onNode(hasText(string(diceType.labelResId)) and isSelectable())

    private fun setContent(
        initialDiceType: DiceType = DiceType.SINGLE_D6,
        onDiceTypeSelected: (DiceType) -> Unit = {}
    ) {
        composeTestRule.setContent {
            DiceRollerTheme {
                var selectedDiceType by remember { mutableStateOf(initialDiceType) }
                var isPickerVisible by remember { mutableStateOf(false) }

                Scaffold(
                    floatingActionButton = {
                        DiceTypeFab(
                            selectedDiceType = selectedDiceType,
                            onClick = { isPickerVisible = true }
                        )
                    }
                ) {
                    if (isPickerVisible) {
                        DiceTypePickerSheet(
                            selectedDiceType = selectedDiceType,
                            onDiceTypeSelected = { diceType ->
                                selectedDiceType = diceType
                                onDiceTypeSelected(diceType)
                            },
                            onDismissRequest = { isPickerVisible = false }
                        )
                    }
                }
            }
        }
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private companion object {
        const val SHEET_DISMISS_TIMEOUT_MILLIS = 5_000L
    }
}
