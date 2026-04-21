package com.greenfodor.diceroller.ui.die.d6

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.LocalDiceColors

@Composable
fun RollingCubeAnimation(
    cubeState: CubeState,
    modifier: Modifier = Modifier
) {
    val diceColors = LocalDiceColors.current
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

    // Publish rolling state back to the caller via the state object
    cubeState.isRolling = rotationX != cubeState.targetRotationX ||
            rotationY != cubeState.targetRotationY

    Canvas(
        modifier = modifier
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
}
