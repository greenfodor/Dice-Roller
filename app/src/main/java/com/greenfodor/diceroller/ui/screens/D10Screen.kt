package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.dice.d10.D10
import com.greenfodor.diceroller.ui.dice.d10.RollingD10Animation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun D10Screen(onRollSettled: (RollOutcome) -> Unit = {}) {
    val dieState = rememberDieState(die = D10)
    DiceScreen(
        dieStates = listOf(dieState),
        dieLabel = DieLabels.D10,
        onRollSettled = onRollSettled,
        rollButtonResId = R.string.roll_button_single
    ) { state ->
        RollingD10Animation(dieState = state)
    }
}

@LightDarkPreview
@Composable
private fun D10ScreenPreview() {
    DiceRollerTheme {
        D10Screen()
    }
}
