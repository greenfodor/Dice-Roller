package com.greenfodor.diceroller.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.dice.d8.D8
import com.greenfodor.diceroller.ui.dice.d8.RollingD8Animation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
fun D8Screen() {
    val dieState = rememberDieState(die = D8)
    DiceScreen(
        dieStates = listOf(dieState),
        rollButtonResId = R.string.roll_button_single
    ) { state ->
        RollingD8Animation(dieState = state)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun D8ScreenPreview() {
    DiceRollerTheme {
        D8Screen()
    }
}
