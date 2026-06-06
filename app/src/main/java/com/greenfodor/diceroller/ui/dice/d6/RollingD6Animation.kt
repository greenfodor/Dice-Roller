package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.greenfodor.diceroller.ui.dice.DieState
import com.greenfodor.diceroller.ui.theme.LocalDiceColors
import com.greenfodor.diceroller.ui.theme.diceSpecs
import com.greenfodor.diceroller.ui.theme.spacing

@Composable
fun RollingCubeAnimation(
    cubeState: DieState,
    modifier: Modifier = Modifier
) {
    val diceColors = LocalDiceColors.current
    val diceSpecs = MaterialTheme.diceSpecs
    val paints = remember { D6Paints() }

    val rotationX by animateFloatAsState(
        targetValue = cubeState.targetRotationX,
        animationSpec = tween(
            durationMillis = diceSpecs.rollDurationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = cubeState.targetRotationY,
        animationSpec = tween(
            durationMillis = diceSpecs.rollDurationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "rotationY"
    )

    val rotationZ by animateFloatAsState(
        targetValue = cubeState.targetRotationZ,
        animationSpec = tween(
            durationMillis = diceSpecs.rollDurationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "rotationZ"
    )

    // Publish rolling state back to the caller via the state object
    cubeState.isRolling = rotationX != cubeState.targetRotationX ||
        rotationY != cubeState.targetRotationY ||
        rotationZ != cubeState.targetRotationZ

    Canvas(
        modifier = modifier
            .size(diceSpecs.canvasSize)
            .padding(MaterialTheme.spacing.medium)
            .graphicsLayer { clip = false }
    ) {
        drawD6(
            size = diceSpecs.diceInternalSize,
            centerX = size.width / 2,
            centerY = size.height / 2,
            rotationX = rotationX,
            rotationY = rotationY,
            rotationZ = rotationZ,
            paints = paints,
            diceColors = diceColors
        )
    }
}
