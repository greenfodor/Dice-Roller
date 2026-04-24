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
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.dice.d6.CubeState
import com.greenfodor.diceroller.ui.theme.spacing

@Composable
fun RollingD20Animation(
    diceState: CubeState,
    modifier: Modifier = Modifier
) {
    val facePath = remember { Path() }
    val paints = remember { D20Paints() }
    val primaryColor = MaterialTheme.colorScheme.primary

    val rotationX by animateFloatAsState(
        targetValue = diceState.targetRotationX,
        animationSpec = tween(
            durationMillis = DiceConstants.ROLL_DURATION_MILLIS,
            easing = FastOutSlowInEasing
        ),
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = diceState.targetRotationY,
        animationSpec = tween(
            durationMillis = DiceConstants.ROLL_DURATION_MILLIS,
            easing = FastOutSlowInEasing
        ),
        label = "rotationY"
    )

    val rotationZ by animateFloatAsState(
        targetValue = diceState.targetRotationZ,
        animationSpec = tween(
            durationMillis = DiceConstants.ROLL_DURATION_MILLIS,
            easing = FastOutSlowInEasing
        ),
        label = "rotationZ"
    )

    diceState.isRolling = rotationX != diceState.targetRotationX ||
            rotationY != diceState.targetRotationY ||
            rotationZ != diceState.targetRotationZ

    Canvas(
        modifier = modifier
            .size(200.dp)
            .padding(MaterialTheme.spacing.medium)
            .graphicsLayer { clip = false }
    ) {
        drawD20(
            size = DiceConstants.DEFAULT_CUBE_SIZE,
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
