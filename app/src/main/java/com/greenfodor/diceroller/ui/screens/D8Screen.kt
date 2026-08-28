package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.dice.d8.D8
import com.greenfodor.diceroller.ui.dice.d8.RollingD8Animation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun D8Screen(onRollSettled: (RollOutcome) -> Unit = {}) {
    val dieState = rememberDieState(die = D8)
    DiceScreen(
        dieStates = listOf(dieState),
        dieLabel = DieLabels.D8,
        onRollSettled = onRollSettled,
        rollButtonResId = R.string.roll_button_single
    ) { state ->
        RollingD8Animation(dieState = state)
    }
}

@LightDarkPreview
@Composable
private fun D8ScreenPreview() {
    DiceRollerTheme {
        D8Screen()
    }
}
