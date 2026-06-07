package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.dice.d6.D6
import com.greenfodor.diceroller.ui.dice.d6.RollingCubeAnimation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun D6Screen() {
    val dieState = rememberDieState(die = D6)
    DiceScreen(
        dieStates = listOf(dieState),
        rollButtonResId = R.string.roll_button_single
    ) { state ->
        RollingCubeAnimation(cubeState = state)
    }
}

@LightDarkPreview
@Composable
private fun D6ScreenPreview() {
    DiceRollerTheme {
        D6Screen()
    }
}
