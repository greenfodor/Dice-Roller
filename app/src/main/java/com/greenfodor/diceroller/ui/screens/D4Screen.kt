package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.dice.d4.D4
import com.greenfodor.diceroller.ui.dice.d4.RollingD4Animation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun D4Screen(onRollSettled: (RollOutcome) -> Unit = {}) {
    val dieState = rememberDieState(die = D4)
    DiceScreen(
        dieStates = listOf(dieState),
        dieLabel = DieLabels.D4,
        onRollSettled = onRollSettled,
        rollButtonResId = R.string.roll_button_single
    ) { state ->
        RollingD4Animation(dieState = state)
    }
}

@LightDarkPreview
@Composable
private fun D4ScreenPreview() {
    DiceRollerTheme {
        D4Screen()
    }
}
