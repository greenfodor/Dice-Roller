package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.dice.d10.RollingD10Animation
import com.greenfodor.diceroller.ui.dice.d100.PercentileTensDie
import com.greenfodor.diceroller.ui.dice.d100.PercentileUnitsDie
import com.greenfodor.diceroller.ui.dice.d100.percentileTotal
import com.greenfodor.diceroller.ui.dice.d100.tensLabel
import com.greenfodor.diceroller.ui.dice.d100.unitsLabel
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

/**
 * Standard D&D d100: two d10s (a tens die marked 00–90 and a units die marked 0–9)
 * rolled together, summed into a 1–100 percentile result.
 */
@Composable
fun D100Screen(onRollSettled: (RollOutcome) -> Unit = {}) {
    val tensState = rememberDieState(die = PercentileTensDie)
    val unitsState = rememberDieState(die = PercentileUnitsDie)
    DiceScreen(
        dieStates = listOf(tensState, unitsState),
        dieLabel = DieLabels.D100,
        onRollSettled = onRollSettled,
        rollButtonResId = R.string.roll_button_multiple,
        result = ::percentileTotal
    ) { state ->
        RollingD10Animation(
            dieState = state,
            colorTarget = if (state === tensState) DieColorTarget.D100 else DieColorTarget.D100_SECONDARY,
            labelFor = if (state === tensState) ::tensLabel else ::unitsLabel
        )
    }
}

@LightDarkPreview
@Composable
private fun D100ScreenPreview() {
    DiceRollerTheme {
        D100Screen()
    }
}
