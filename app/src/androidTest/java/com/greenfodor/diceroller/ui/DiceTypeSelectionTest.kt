package com.greenfodor.diceroller.ui

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.greenfodor.diceroller.FakeSettingsRepository
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.fakeDataKoinTestRule
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.cd_change_die_type
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.cd_die_value
import com.greenfodor.diceroller.ui.screens.DiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private class NoRollHistoryRepository : RollHistoryRepository {
    override val rolls = MutableStateFlow(emptyList<RollRecord>())

    override suspend fun record(record: RollRecord) = Unit

    override suspend fun clear() = Unit
}

/**
 * Drives the real [DiceRollerApp] to check that the die type picked on the dice screen is both
 * rendered straight away and restored from the persisted selection on the next launch.
 */
class DiceTypeSelectionTest {
    private val fakeSettings = FakeSettingsRepository()

    @get:Rule(order = 0)
    val koinTestRule = fakeDataKoinTestRule(NoRollHistoryRepository(), fakeSettings)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun pickingADieTypeSwapsTheDiceScreenWithoutLeavingIt() {
        setContent()

        assertEquals(1, diceOnScreen())

        pick(DiceType.DOUBLE_D6)

        assertEquals(2, diceOnScreen())
    }

    @Test
    fun pickingADieTypePersistsItAsTheSelection() {
        setContent()

        pick(DiceType.SINGLE_D20)

        assertEquals(DiceType.SINGLE_D20.name, fakeSettings.selectedDiceType.value)
    }

    @Test
    fun theDiceScreenOpensOnThePersistedDieType() {
        fakeSettings.selectedDiceType.value = DiceType.DOUBLE_D6.name

        setContent()

        assertEquals(2, diceOnScreen())
        expandPicker()
        tile(DiceType.DOUBLE_D6).assertIsSelected()
    }

    /** How many dice the current screen draws — one for a single die, two for 2d6 and d100. */
    private fun diceOnScreen(): Int {
        val prefix = string(Res.string.cd_die_value, 0).substringBeforeLast(' ')
        return composeTestRule
            .onAllNodes(hasContentDescription(prefix, substring = true))
            .fetchSemanticsNodes()
            .size
    }

    private fun pick(diceType: DiceType) {
        expandPicker()
        tile(diceType).performClick()
        composeTestRule.waitForIdle()
    }

    private fun expandPicker() {
        composeTestRule.onNodeWithContentDescription(string(Res.string.cd_change_die_type)).performClick()
        composeTestRule.waitForIdle()
    }

    private fun tile(diceType: DiceType) =
        composeTestRule.onNode(hasText(string(diceType.label)) and isSelectable())

    private fun setContent() {
        composeTestRule.setContent { DiceRollerApp(windowSizeClass = CompactWindowSizeClass) }
    }

    private fun string(resource: StringResource, vararg formatArgs: Any): String =
        runBlocking { getString(resource, *formatArgs) }
}
