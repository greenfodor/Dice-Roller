package com.greenfodor.diceroller.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.sensors.performRollHaptics
import com.greenfodor.diceroller.ui.die.d6.RollingCubeAnimation
import com.greenfodor.diceroller.ui.die.d6.rememberCubeState
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.utils.rememberShakeDetector

@Composable
fun D6Screen() {
    val context = LocalContext.current
    val cubeState = rememberCubeState()

    rememberShakeDetector(onShake = {
        if (cubeState.isRolling.not()) {
            performRollHaptics(context)
            cubeState.roll()
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RollingCubeAnimation(cubeState = cubeState)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                performRollHaptics(context)
                cubeState.roll()
            },
            enabled = cubeState.isRolling.not()
        ) {
            Text(text = stringResource(R.string.roll_button_single))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun D6ScreenPreview() {
    DiceRollerTheme {
        D6Screen()
    }
}