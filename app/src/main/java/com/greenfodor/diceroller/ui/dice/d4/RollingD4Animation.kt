package com.greenfodor.diceroller.ui.dice.d4

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
import com.greenfodor.diceroller.ui.theme.diceSpecs
import com.greenfodor.diceroller.ui.theme.spacing

@Composable
fun RollingD4Animation(
    dieState: DieState,
    modifier: Modifier = Modifier
) {
    val diceSpecs = MaterialTheme.diceSpecs
    val paints = remember { D4Paints() }
    val color = MaterialTheme.colorScheme.tertiary
    val rotation = rememberRollRotation(dieState)

    Canvas(
        modifier = modifier
            .size(diceSpecs.canvasSize)
            .padding(MaterialTheme.spacing.medium)
            .graphicsLayer { clip = false }
    ) {
        drawD4(
            size = diceSpecs.diceInternalSize,
            centerX = size.width / 2,
            centerY = size.height / 2,
            rotationX = rotation.x.value,
            rotationY = rotation.y.value,
            rotationZ = rotation.z.value,
            paints = paints,
            color = color
        )
    }
}
