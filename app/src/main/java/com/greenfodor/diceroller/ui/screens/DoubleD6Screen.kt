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
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.sensors.performRollHaptics
import com.greenfodor.diceroller.ui.die.d6.RollingCubeAnimation
import com.greenfodor.diceroller.ui.die.d6.rememberCubeState
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.utils.rememberShakeDetector

@Composable
fun DoubleD6Screen() {
    val context = LocalContext.current
    val firstCubeState = rememberCubeState()
    val secondCubeState = rememberCubeState()

    rememberShakeDetector(onShake = {
        if (firstCubeState.isRolling.not() && secondCubeState.isRolling.not()) {
            performRollHaptics(context)
            firstCubeState.roll()
            secondCubeState.roll()
        }
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

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                performRollHaptics(context)
                firstCubeState.roll()
                secondCubeState.roll()
            },
            enabled = firstCubeState.isRolling.not() && secondCubeState.isRolling.not()
        ) {
            Text(text = stringResource(R.string.roll_button_multiple))
        }

        Spacer(modifier = Modifier.height(16.dp))
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