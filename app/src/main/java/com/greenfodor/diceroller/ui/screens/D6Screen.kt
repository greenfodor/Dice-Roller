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
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.die.d6.RollingCubeAnimation
import com.greenfodor.diceroller.ui.die.d6.rememberCubeState

@Composable
fun D6Screen() {
    val cubeState = rememberCubeState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RollingCubeAnimation(cubeState = cubeState)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { cubeState.roll() },
            enabled = cubeState.isRolling.not()
        ) {
            Text("Roll Cube")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}