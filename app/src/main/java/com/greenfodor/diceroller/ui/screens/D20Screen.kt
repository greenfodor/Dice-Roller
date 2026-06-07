package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.dice.d20.D20
import com.greenfodor.diceroller.ui.dice.d20.RollingD20Animation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun D20Screen() {
    val dieState = rememberDieState(die = D20)
    DiceScreen(
        dieStates = listOf(dieState),
        rollButtonResId = R.string.roll_button_single
    ) { state ->
        RollingD20Animation(dieState = state)
    }
}

@LightDarkPreview
@Composable
private fun D20ScreenPreview() {
    DiceRollerTheme {
        D20Screen()
    }
}
