package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.greenfodor.diceroller.ui.dice.DieState
import com.greenfodor.diceroller.ui.dice.rememberRollRotation
import com.greenfodor.diceroller.ui.theme.LocalDiceColors
import com.greenfodor.diceroller.ui.theme.diceSpecs
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.utils.LocalD6FaceStyle

@Composable
fun RollingCubeAnimation(
    cubeState: DieState,
    modifier: Modifier = Modifier
) {
    val diceColors = LocalDiceColors.current
    val faceStyle = LocalD6FaceStyle.current
    val diceSpecs = MaterialTheme.diceSpecs
    val paints = remember { D6Paints() }
    val rotation = rememberRollRotation(cubeState)

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
            rotationX = rotation.x.value,
            rotationY = rotation.y.value,
            rotationZ = rotation.z.value,
            paints = paints,
            diceColors = diceColors,
            faceStyle = faceStyle
        )
    }
}
