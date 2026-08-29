package com.greenfodor.diceroller.ui.history

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.data.RollHistorySection
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.feature.history.presentation.R
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.ZoneId

@RunWith(AndroidJUnit4::class)
class RollHistoryContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyStateShowsTheNoRollsMessage() {
        setContent(RollHistoryUiState.Empty)

        composeTestRule.onNodeWithText(string(R.string.roll_history_empty)).assertIsDisplayed()
    }

    @Test
    fun emptyStateShowsNoRollRows() {
        setContent(RollHistoryUiState.Empty)

        composeTestRule.onNodeWithText(DieLabels.D20).assertDoesNotExist()
    }

    @Test
    fun contentStateShowsTheDieNotationOfEveryRoll() {
        setContent(contentState())

        composeTestRule.onNodeWithText(DieLabels.DOUBLE_D6).assertIsDisplayed()
        composeTestRule.onNodeWithText(DieLabels.D20).assertIsDisplayed()
    }

    @Test
    fun contentStateShowsTheScoredTotalOfEveryRoll() {
        setContent(contentState())

        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithText("20").assertIsDisplayed()
    }

    @Test
    fun contentStateShowsTheIndividualValuesOfAMultiDieRoll() {
        setContent(contentState())

        composeTestRule.onNodeWithText("4 + 6", substring = true).assertIsDisplayed()
    }

    @Test
    fun contentStateShowsTodayAndYesterdaySectionHeaders() {
        setContent(contentState())

        composeTestRule.onNodeWithText(string(R.string.roll_history_today)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.roll_history_yesterday)).assertIsDisplayed()
    }

    @Test
    fun anOlderSectionHeaderShowsTheFormattedDateInsteadOfTodayOrYesterday() {
        setContent(
            RollHistoryUiState.Content(
                listOf(
                    RollHistorySection(
                        day = RollHistoryDay.Earlier(LocalDate.of(2026, 8, 20)),
                        rolls = listOf(record(id = 1, dieLabel = DieLabels.D8, values = listOf(7), total = 7))
                    )
                )
            )
        )

        composeTestRule.onNodeWithText(string(R.string.roll_history_today)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.roll_history_yesterday)).assertDoesNotExist()
        composeTestRule.onNodeWithText(DieLabels.D8).assertIsDisplayed()
    }

    @Test
    fun errorStateShowsTheCouldNotLoadMessage() {
        setContent(RollHistoryUiState.Error)

        composeTestRule.onNodeWithText(string(R.string.roll_history_error)).assertIsDisplayed()
    }

    @Test
    fun errorStateShowsNoRollRows() {
        setContent(RollHistoryUiState.Error)

        composeTestRule.onNodeWithText(DieLabels.D20).assertDoesNotExist()
    }

    @Test
    fun loadingStateShowsASpinner() {
        setContent(RollHistoryUiState.Loading)

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    @Test
    fun theTitleIsShownInEveryState() {
        var state: RollHistoryUiState by mutableStateOf(RollHistoryUiState.Loading)
        composeTestRule.setContent {
            DiceRollerTheme {
                RollHistoryContent(state = state, zoneId = ZONE, onDismiss = {})
            }
        }

        listOf(
            RollHistoryUiState.Loading,
            RollHistoryUiState.Empty,
            RollHistoryUiState.Error,
            contentState()
        ).forEach { next ->
            composeTestRule.runOnIdle { state = next }

            composeTestRule.onNodeWithText(string(R.string.roll_history_title)).assertIsDisplayed()
        }
    }

    @Test
    fun clickingCloseInvokesTheDismissCallback() {
        var dismissed = false
        setContent(contentState(), onDismiss = { dismissed = true })

        composeTestRule.onNodeWithText(string(R.string.roll_history_close)).performClick()

        assertEquals(true, dismissed)
    }

    @Test
    fun theTodayHeaderStaysPinnedWhileItsRollsScrollUnderneathIt() {
        val manyRolls = (1..LONG_HISTORY_SIZE).map { index ->
            record(id = index.toLong(), dieLabel = "roll$index", values = listOf(1), total = 1)
        }
        setContent(
            RollHistoryUiState.Content(
                listOf(RollHistorySection(day = RollHistoryDay.Today, rolls = manyRolls))
            )
        )

        composeTestRule.onNodeWithText("roll1").assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.roll_history_today)).assertIsDisplayed()

        composeTestRule.onNode(hasScrollAction()).performScrollToIndex(LONG_HISTORY_SIZE)

        composeTestRule.onNodeWithText("roll1").assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.roll_history_today)).assertIsDisplayed()
    }

    @Test
    fun aLongHistoryKeepsTheTitleAndCloseButtonOnScreen() {
        val manyRolls = (1..LONG_HISTORY_SIZE).map { index ->
            record(id = index.toLong(), dieLabel = "roll$index", values = listOf(1), total = 1)
        }
        setContent(
            RollHistoryUiState.Content(
                listOf(RollHistorySection(day = RollHistoryDay.Today, rolls = manyRolls))
            )
        )

        composeTestRule.onNodeWithText(string(R.string.roll_history_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.roll_history_close)).assertIsDisplayed()
    }

    @Test
    fun aShortHistoryStillKeepsTheCloseButtonOnScreen() {
        setContent(contentState())

        composeTestRule.onNodeWithText(string(R.string.roll_history_close)).assertIsDisplayed()
    }

    @Test
    fun rowsStillRenderWhenTheHeightIsUnbounded() {
        composeTestRule.setContent {
            DiceRollerTheme {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    RollHistoryContent(state = contentState(), zoneId = ZONE, onDismiss = {})
                }
            }
        }

        composeTestRule.onNodeWithText(DieLabels.DOUBLE_D6).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.roll_history_today)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.roll_history_close)).assertIsDisplayed()
    }

    @Test
    fun aLongHistoryStaysBoundedWhenTheHeightIsUnbounded() {
        val manyRolls = (1..LONG_HISTORY_SIZE).map { index ->
            record(id = index.toLong(), dieLabel = "roll$index", values = listOf(1), total = 1)
        }
        composeTestRule.setContent {
            DiceRollerTheme {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    RollHistoryContent(
                        state = RollHistoryUiState.Content(
                            listOf(RollHistorySection(day = RollHistoryDay.Today, rolls = manyRolls))
                        ),
                        zoneId = ZONE,
                        onDismiss = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("roll1").assertIsDisplayed()
        composeTestRule.onNodeWithText("roll$LONG_HISTORY_SIZE").assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.roll_history_close)).assertIsDisplayed()
    }

    private fun setContent(
        state: RollHistoryUiState,
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            DiceRollerTheme {
                RollHistoryContent(state = state, zoneId = ZONE, onDismiss = onDismiss)
            }
        }
    }

    private fun contentState(): RollHistoryUiState.Content = RollHistoryUiState.Content(
        sections = listOf(
            RollHistorySection(
                day = RollHistoryDay.Today,
                rolls = listOf(
                    record(id = 3, dieLabel = DieLabels.DOUBLE_D6, values = listOf(4, 6), total = 10),
                    record(id = 2, dieLabel = DieLabels.D20, values = listOf(20), total = 20)
                )
            ),
            RollHistorySection(
                day = RollHistoryDay.Yesterday,
                rolls = listOf(record(id = 1, dieLabel = DieLabels.D4, values = listOf(3), total = 3))
            )
        )
    )

    private fun record(id: Long, dieLabel: String, values: List<Int>, total: Int): RollRecord =
        RollRecord(
            id = id,
            dieLabel = dieLabel,
            values = values,
            total = total,
            timestampMillis = FIXED_TIMESTAMP_MILLIS
        )

    private fun string(
        @StringRes resId: Int
    ): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private companion object {
        val ZONE: ZoneId = ZoneId.of("Europe/Bucharest")
        const val FIXED_TIMESTAMP_MILLIS = 1_787_000_000_000L
        const val LONG_HISTORY_SIZE = 40
    }
}
