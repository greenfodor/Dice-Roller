package com.greenfodor.diceroller.ui.die.d6

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.geometry.CubeFace
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.LocalDiceColors

@Composable
private fun DiceStaticPreview(
    rotationX: Float,
    rotationY: Float,
    modifier: Modifier = Modifier
) {
    val diceColors = LocalDiceColors.current
    val facePath = remember { Path() }
    val dotPath = remember { Path() }
    val paints = remember { CubePaints() }

    Box(
        modifier = modifier
            .size(200.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .graphicsLayer { clip = false }
        ) {
            drawCube(
                size = DiceConstants.DEFAULT_CUBE_SIZE * 0.6f,
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
}

@Preview(showBackground = true, name = "Front Face")
@Composable
fun PreviewFront() {
    DiceRollerTheme {
        DiceStaticPreview(rotationX = CubeFace.FRONT.rotationX, rotationY = CubeFace.FRONT.rotationY)
    }
}

@Preview(showBackground = true, name = "Angled View")
@Composable
fun PreviewAngled() {
    DiceRollerTheme {
        DiceStaticPreview(rotationX = 45f, rotationY = 45f)
    }
}

@Preview(showBackground = true, name = "Top-Right Edge")
@Composable
fun PreviewTopRight() {
    DiceRollerTheme {
        DiceStaticPreview(rotationX = 30f, rotationY = -30f)
    }
}

@Preview(showBackground = true, name = "Dice Faces Grid")
@Composable
fun PreviewDiceGrid() {
    DiceRollerTheme {
        Column {
            Row {
                DiceStaticPreview(rotationX = CubeFace.FRONT.rotationX, rotationY = CubeFace.FRONT.rotationY)
                DiceStaticPreview(rotationX = CubeFace.BACK.rotationX, rotationY = CubeFace.BACK.rotationY)
            }
            Row {
                DiceStaticPreview(rotationX = CubeFace.TOP.rotationX, rotationY = CubeFace.TOP.rotationY)
                DiceStaticPreview(rotationX = CubeFace.BOTTOM.rotationX, rotationY = CubeFace.BOTTOM.rotationY)
            }
            Row {
                DiceStaticPreview(rotationX = CubeFace.LEFT.rotationX, rotationY = CubeFace.LEFT.rotationY)
                DiceStaticPreview(rotationX = CubeFace.RIGHT.rotationX, rotationY = CubeFace.RIGHT.rotationY)
            }
        }
    }
}
