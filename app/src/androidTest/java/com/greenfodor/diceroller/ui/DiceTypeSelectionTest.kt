package com.greenfodor.diceroller.ui

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.FakeSettingsRepository
import com.greenfodor.diceroller.HiltTestActivity
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.di.DataModule
import com.greenfodor.diceroller.ui.screens.DiceType
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.ZoneId

private class NoRollHistoryRepository : RollHistoryRepository {
    override val rolls = MutableStateFlow(emptyList<RollRecord>())

    override suspend fun record(record: RollRecord) = Unit

    override suspend fun clear() = Unit
}

/**
 * Drives the real [DiceRollerApp] to check that the die type picked on the dice screen is both
 * rendered straight away and restored from the persisted selection on the next launch.
 */
@HiltAndroidTest
@UninstallModules(DataModule::class)
class DiceTypeSelectionTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @BindValue
    val repository: RollHistoryRepository = NoRollHistoryRepository()

    private val fakeSettings = FakeSettingsRepository()

    @BindValue
    val settingsRepository: SettingsRepository = fakeSettings

    @BindValue
    val clock: Clock = Clock.systemDefaultZone()

    @BindValue
    val zoneId: ZoneId = ZoneId.systemDefault()

    @BindValue
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Before
    fun inject() {
        hiltRule.inject()
    }

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
        val prefix = string(R.string.cd_die_value, 0).substringBeforeLast(' ')
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
        composeTestRule.onNodeWithContentDescription(string(R.string.cd_change_die_type)).performClick()
        composeTestRule.waitForIdle()
    }

    private fun tile(diceType: DiceType) =
        composeTestRule.onNode(hasText(string(diceType.labelResId)) and isSelectable())

    private fun setContent() {
        composeTestRule.setContent { DiceRollerApp(windowSizeClass = CompactWindowSizeClass) }
    }

    private fun string(resId: Int, vararg formatArgs: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId, *formatArgs)
}
