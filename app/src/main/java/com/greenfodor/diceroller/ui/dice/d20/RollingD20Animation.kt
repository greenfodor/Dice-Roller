package com.greenfodor.diceroller.ui.dice.d20

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import com.greenfodor.diceroller.ui.dice.d6.CubeState
import com.greenfodor.diceroller.ui.theme.diceSpecs
import com.greenfodor.diceroller.ui.theme.spacing

/**
 * Animated D20 component that responds to changes in [diceState].
 *
 * It uses [animateFloatAsState] for each axis (X, Y, Z) to provide a smooth,
 * high-framerate 3D rolling effect.
 */
@Composable
fun RollingD20Animation(
    diceState: CubeState,
    modifier: Modifier = Modifier
) {
    val diceSpecs = MaterialTheme.diceSpecs
    val facePath = remember { Path() }
    val paints = remember { D20Paints() }
    val primaryColor = MaterialTheme.colorScheme.primary

    val rotationX by animateFloatAsState(
        targetValue = diceState.targetRotationX,
        animationSpec =
            tween(
                durationMillis = diceSpecs.rollDurationMillis,
                easing = FastOutSlowInEasing
            ),
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = diceState.targetRotationY,
        animationSpec =
            tween(
                durationMillis = diceSpecs.rollDurationMillis,
                easing = FastOutSlowInEasing
            ),
        label = "rotationY"
    )

    val rotationZ by animateFloatAsState(
        targetValue = diceState.targetRotationZ,
        animationSpec =
            tween(
                durationMillis = diceSpecs.rollDurationMillis,
                easing = FastOutSlowInEasing
            ),
        label = "rotationZ"
    )

    diceState.isRolling = rotationX != diceState.targetRotationX ||
        rotationY != diceState.targetRotationY ||
        rotationZ != diceState.targetRotationZ

    Canvas(
        modifier =
            modifier
                .size(diceSpecs.canvasSize)
                .padding(MaterialTheme.spacing.medium)
                .graphicsLayer { clip = false }
    ) {
        drawD20(
            size = diceSpecs.diceInternalSize,
            centerX = size.width / 2,
            centerY = size.height / 2,
            rotationX = rotationX,
            rotationY = rotationY,
            rotationZ = rotationZ,
            facePath = facePath,
            paints = paints,
            color = primaryColor
        )
    }
}
