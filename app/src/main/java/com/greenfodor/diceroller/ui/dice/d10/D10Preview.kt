package com.greenfodor.diceroller.ui.dice.d10

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
private fun D10StaticPreview(
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    modifier: Modifier = Modifier
) {
    val paints = remember { D10Paints() }
    val color = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .size(150.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .graphicsLayer { clip = false }
        ) {
            drawD10(
                size = DiceConstants.DEFAULT_CUBE_SIZE * 0.6f,
                centerX = size.width / 2,
                centerY = size.height / 2,
                rotationX = rotationX,
                rotationY = rotationY,
                rotationZ = rotationZ,
                paints = paints,
                color = color
            )
        }
    }
}

@LightDarkPreview
@Composable
fun D10PreviewFace1() {
    DiceRollerTheme {
        val face = D10.faces.first { it.value == 1 }
        D10StaticPreview(rotationX = face.rotationX, rotationY = face.rotationY, rotationZ = face.rotationZ)
    }
}

@LightDarkPreview
@Composable
fun D10PreviewFace10() {
    DiceRollerTheme {
        val face = D10.faces.first { it.value == 10 }
        D10StaticPreview(rotationX = face.rotationX, rotationY = face.rotationY, rotationZ = face.rotationZ)
    }
}

@LightDarkPreview
@Composable
fun D10PreviewAngled() {
    DiceRollerTheme {
        D10StaticPreview(rotationX = 30f, rotationY = 45f, rotationZ = 0f)
    }
}
