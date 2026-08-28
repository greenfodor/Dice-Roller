package com.greenfodor.diceroller.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.FakeSettingsRepository
import com.greenfodor.diceroller.HiltTestActivity
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.di.DataModule
import com.greenfodor.diceroller.ui.components.DiceRollerTopBar
import com.greenfodor.diceroller.ui.history.RollHistoryRoute
import com.greenfodor.diceroller.ui.history.rollHistoryEntry
import com.greenfodor.diceroller.ui.screens.DiceRoute
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
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

@HiltAndroidTest
@UninstallModules(DataModule::class)
class RollHistoryNavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @BindValue
    val repository: RollHistoryRepository = FakeRollHistoryRepository()

    @BindValue
    val settingsRepository: SettingsRepository = FakeSettingsRepository()

    @BindValue
    val clock: Clock = Clock.systemDefaultZone()

    @Before
    fun inject() {
        hiltRule.inject()
    }

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
        composeTestRule.onNodeWithText(DieLabels.DOUBLE_D6).assertIsDisplayed()
    }

    @Test
    fun theHistoryEntryResolvesItsViewModelThroughHilt() {
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(sampleRoll.total.toString()).assertIsDisplayed()
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
        (repository as FakeRollHistoryRepository).state.value = emptyList()
        setContent()

        composeTestRule.onNodeWithContentDescription(string(R.string.cd_open_roll_history)).performClick()

        composeTestRule.onNodeWithText(string(HistoryR.string.roll_history_empty)).assertIsDisplayed()
    }

    private fun setContent() {
        composeTestRule.setContent {
            DiceRollerTheme { TestNavDisplay() }
        }
    }

    @Composable
    private fun TestNavDisplay() {
        val backStack = rememberNavBackStack(DiceRoute)
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            sceneStrategies = listOf(DialogSceneStrategy()),
            entryProvider = entryProvider {
                entry<DiceRoute> {
                    DiceRollerTopBar(
                        selectedDiceType = DiceType.SINGLE_D6,
                        onDiceTypeSelected = {},
                        onOpenHistory = { backStack.add(RollHistoryRoute) },
                        onOpenSettings = {}
                    )
                }
                rollHistoryEntry(onDismiss = { backStack.removeLastOrNull() })
            }
        )
    }

    private fun string(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
}
