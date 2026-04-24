package com.greenfodor.diceroller.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.dice.d20.D20
import com.greenfodor.diceroller.ui.dice.d20.RollingD20Animation
import com.greenfodor.diceroller.ui.dice.d6.CubeState
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.utils.rememberShakeDetector
import com.greenfodor.diceroller.ui.utils.rollDice

@Composable
fun D20Screen() {
    val context = LocalContext.current
    val diceState = remember { CubeState(die = D20) }

    rememberShakeDetector(onShake = {
        context.rollDice(diceState)
    })

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        RollingD20Animation(diceState = diceState)

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Button(
            onClick = { context.rollDice(diceState) },
            enabled = diceState.isRolling.not(),
        ) {
            Text(text = stringResource(R.string.roll_button_single))
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun D20ScreenPreview() {
    DiceRollerTheme {
        D20Screen()
    }
}
