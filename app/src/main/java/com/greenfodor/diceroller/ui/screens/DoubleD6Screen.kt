package com.greenfodor.diceroller.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.dice.d6.D6
import com.greenfodor.diceroller.ui.dice.d6.RollingCubeAnimation
import com.greenfodor.diceroller.ui.dice.rememberDieState
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.utils.rememberShakeDetector
import com.greenfodor.diceroller.ui.utils.rollDice

@Composable
fun DoubleD6Screen() {
    val context = LocalContext.current
    val firstCubeState = rememberDieState(die = D6)
    val secondCubeState = rememberDieState(die = D6)

    rememberShakeDetector(onShake = {
        context.rollDice(firstCubeState, secondCubeState)
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RollingCubeAnimation(cubeState = firstCubeState)
            RollingCubeAnimation(cubeState = secondCubeState)
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Button(
            onClick = { context.rollDice(firstCubeState, secondCubeState) },
            enabled = firstCubeState.isRolling.not() && secondCubeState.isRolling.not()
        ) {
            Text(text = stringResource(R.string.roll_button_multiple))
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
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
