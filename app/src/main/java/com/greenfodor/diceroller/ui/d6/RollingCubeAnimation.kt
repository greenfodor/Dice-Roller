package com.greenfodor.diceroller.ui.d6

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.LocalDiceColors

@Composable
fun RollingCubeAnimation() {
    val diceColors = LocalDiceColors.current
    val cubeState = rememberCubeState()

    // Reusable draw objects — allocated once, never recreated on recomposition
    val facePath = remember { Path() }
    val dotPath = remember { Path() }
    val paints = remember { CubePaints() }

    val rotationX by animateFloatAsState(
        targetValue = cubeState.targetRotationX,
        animationSpec = tween(
            durationMillis = DiceConstants.ROLL_DURATION_MILLIS,
            easing = FastOutSlowInEasing
        ),
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = cubeState.targetRotationY,
        animationSpec = tween(
            durationMillis = DiceConstants.ROLL_DURATION_MILLIS,
            easing = FastOutSlowInEasing
        ),
        label = "rotationY"
    )

    val isRolling = rotationX != cubeState.targetRotationX ||
                    rotationY != cubeState.targetRotationY

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
                .graphicsLayer { clip = false }
        ) {
            drawCube(
                size = DiceConstants.DEFAULT_CUBE_SIZE,
                centerX = size.width / 2,
                centerY = size.height / 2,
                rotationX = rotationX,
                rotationY = rotationY,
                facePath = facePath,
                dotPath = dotPath,
                paints = paints,
                diceColors = diceColors
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { cubeState.roll() },
            enabled = !isRolling
        ) {
            Text("Roll Cube")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
