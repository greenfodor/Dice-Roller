package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiceTypeRailTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theRailShowsOneTileForEveryDiceTypeEntryAndNoFab() {
        setContent()

        DiceType.entries.forEach { diceType ->
            tile(diceType).assertExists()
        }
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type)).assertDoesNotExist()
    }

    @Test
    fun theTilesAreLaidOutTwoToARow() {
        setContent()

        val rows = DiceType.entries.chunked(RAIL_COLUMNS).filter { it.size == RAIL_COLUMNS }

        rows.forEach { (start, end) ->
            assertEquals(bounds(start).top, bounds(end).top, TOLERANCE_PX)
            assertTrue(bounds(end).left > bounds(start).left)
        }

        rows.zipWithNext { above, below ->
            assertTrue(bounds(below.first()).top > bounds(above.first()).top)
        }

        val firstColumn = rows.map { bounds(it.first()).left }
        val secondColumn = rows.map { bounds(it.last()).left }
        firstColumn.forEach { assertEquals(firstColumn.first(), it, TOLERANCE_PX) }
        secondColumn.forEach { assertEquals(secondColumn.first(), it, TOLERANCE_PX) }
    }

    @Test
    fun everyTileIsSquare() {
        setContent()

        DiceType.entries.forEach { diceType ->
            val tile = tile(diceType).fetchSemanticsNode().size
            assertEquals("$diceType tile", tile.width.toFloat(), tile.height.toFloat(), TOLERANCE_PX)
        }
    }

    @Test
    fun aTrailingLoneTileIsCenteredAcrossBothColumns() {
        setContent()

        val rows = DiceType.entries.chunked(RAIL_COLUMNS)
        val lastRow = rows.last()
        assertEquals("DiceType.entries is even, so no lone tile exists", 1, lastRow.size)

        val loneTile = bounds(lastRow.single())
        val gridStart = bounds(rows.first().first()).left
        val gridEnd = bounds(rows.first().last()).right

        assertEquals((gridStart + gridEnd) / 2f, loneTile.center.x, TOLERANCE_PX)
        assertTrue(loneTile.left > gridStart)
        assertTrue(loneTile.right < gridEnd)
    }

    @Test
    fun theTileForTheActiveDiceTypeIsSelectedAndTheOthersAreNot() {
        setContent(initialDiceType = DiceType.SINGLE_D8)

        tile(DiceType.SINGLE_D8).assertIsSelected()
        tile(DiceType.SINGLE_D6).assertIsNotSelected()
    }

    @Test
    fun tappingATileReportsThatDiceTypeAndLeavesTheRailOnScreen() {
        var reported: DiceType? = null
        setContent(onDiceTypeSelected = { reported = it })

        tile(DiceType.SINGLE_D8).performScrollTo().performClick()

        assertEquals(DiceType.SINGLE_D8, reported)
        tile(DiceType.SINGLE_D8).assertIsSelected()
        tile(DiceType.SINGLE_D8).assertIsDisplayed()
    }

    private fun tile(diceType: DiceType) =
        composeTestRule.onNode(hasText(string(diceType.labelResId)) and isSelectable())

    private fun bounds(diceType: DiceType): Rect = tile(diceType).fetchSemanticsNode().boundsInRoot

    private fun setContent(
        initialDiceType: DiceType = DiceType.SINGLE_D6,
        onDiceTypeSelected: (DiceType) -> Unit = {}
    ) {
        composeTestRule.setContent {
            DiceRollerTheme {
                var selectedDiceType by remember { mutableStateOf(initialDiceType) }

                Row(modifier = Modifier.fillMaxSize()) {
                    DiceTypeRail(
                        selectedDiceType = selectedDiceType,
                        onDiceTypeSelected = { diceType ->
                            selectedDiceType = diceType
                            onDiceTypeSelected(diceType)
                        }
                    )
                }
            }
        }
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private companion object {
        const val RAIL_COLUMNS = 2
        const val TOLERANCE_PX = 1f
    }
}
