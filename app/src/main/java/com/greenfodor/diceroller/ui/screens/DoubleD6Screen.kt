package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.dice.d6.D6
import com.greenfodor.diceroller.ui.dice.d6.RollingCubeAnimation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun DoubleD6Screen(onRollSettled: (RollOutcome) -> Unit = {}) {
    val firstCubeState = rememberDieState(die = D6)
    val secondCubeState = rememberDieState(die = D6)
    DiceScreen(
        dieStates = listOf(firstCubeState, secondCubeState),
        dieLabel = DieLabels.DOUBLE_D6,
        onRollSettled = onRollSettled,
        rollButtonResId = R.string.roll_button_multiple
    ) { state ->
        val target = if (state === secondCubeState) DieColorTarget.D6_SECONDARY else DieColorTarget.D6
        RollingCubeAnimation(cubeState = state, colorTarget = target)
    }
}

@LightDarkPreview
@Composable
private fun DoubleD6ScreenPreview() {
    DiceRollerTheme {
        DoubleD6Screen()
    }
}
