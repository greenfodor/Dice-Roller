package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.dice.d6.D6
import com.greenfodor.diceroller.ui.dice.d6.RollingCubeAnimation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun DoubleD6Screen() {
    val firstCubeState = rememberDieState(die = D6)
    val secondCubeState = rememberDieState(die = D6)
    DiceScreen(
        dieStates = listOf(firstCubeState, secondCubeState),
        rollButtonResId = R.string.roll_button_multiple
    ) { state ->
        RollingCubeAnimation(cubeState = state)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DoubleD6ScreenPreview() {
    DiceRollerTheme {
        DoubleD6Screen()
    }
}
