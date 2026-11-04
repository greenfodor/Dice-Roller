package com.greenfodor.diceroller.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.FakeSettingsRepository
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.fakeDataKoinTestRule
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.settings_theme_title
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.settings_title
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.junit.Rule
import org.junit.Test
import com.greenfodor.diceroller.feature.history.presentation.R as HistoryR

private class FakeRollHistoryRepository : RollHistoryRepository {
    val state = MutableStateFlow(listOf(sampleRoll))
    override val rolls = state

    override suspend fun record(record: RollRecord) {
        state.value = state.value + record
    }

    override suspend fun clear() {
        state.value = emptyList()
    }
}

private val sampleRoll = RollRecord(
    id = 1,
    dieLabel = DieLabels.DOUBLE_D6,
    values = listOf(4, 6),
    total = 10,
    timestampMillis = 1_787_000_000_000L
)

/** Drives the real [DiceRollerApp] back stack, with only the persistence layer faked. */
class RollHistoryNavigationTest {
    private val repository = FakeRollHistoryRepository()

    @get:Rule(order = 0)
    val koinTestRule = fakeDataKoinTestRule(repository, FakeSettingsRepository())

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun theHistoryIsNotShownUntilTheTopBarButtonIsClicked() {
        setContent()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_title)).assertDoesNotExist()
    }

    @Test
    fun clickingTheTopBarHistoryButtonOpensTheHistoryOverTheDiceScreen() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_title)).assertIsDisplayed()
        composeTestRule
            .onNode(hasText(DieLabels.DOUBLE_D6) and isSelectable().not())
            .assertIsDisplayed()
    }

    @Test
    fun theHistoryEntryResolvesItsViewModelThroughKoin() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(sampleRoll.total.toString()).assertIsDisplayed()
    }

    @Test
    fun theDiceScreenShowsTheNewestRollInTheRecentRollsWheel() {
        setContent()

        composeTestRule.onNodeWithText("2d6  10  (4 + 6)").assertIsDisplayed()
    }

    @Test
    fun closingTheHistoryReturnsToTheDiceScreen() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()
        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_close)).performClick()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_title)).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).assertIsDisplayed()
    }

    @Test
    fun anEmptyHistoryOpensOnTheNoRollsMessage() {
        repository.state.value = emptyList()
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_empty)).assertIsDisplayed()
    }

    @Test
    fun theSettingsEntryShowsItsSettingsAsSoonAsItIsOpened() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_settings)).performClick()

        composeTestRule.onNodeWithText(string(Res.string.settings_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(Res.string.settings_theme_title)).assertIsDisplayed()
    }

    private fun setContent() {
        composeTestRule.setContent { DiceRollerApp(windowSizeClass = CompactWindowSizeClass) }
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private fun string(resource: StringResource): String = runBlocking { getString(resource) }
}
