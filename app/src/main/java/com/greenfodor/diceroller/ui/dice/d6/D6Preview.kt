package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.geometry.CubeFace
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.LocalDiceColors

@Composable
private fun D6StaticPreview(
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
            .size(150.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier =
                Modifier
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

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Front Face Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Front Face Dark")
@Composable
fun D6PreviewFront() {
    DiceRollerTheme {
        D6StaticPreview(rotationX = CubeFace.FRONT.rotationX, rotationY = CubeFace.FRONT.rotationY)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Angled View Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Angled View Dark")
@Composable
fun D6PreviewAngled() {
    DiceRollerTheme {
        D6StaticPreview(rotationX = 45f, rotationY = 45f)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Top-Right Edge Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Top-Right Edge Dark")
@Composable
fun D6PreviewTopRight() {
    DiceRollerTheme {
        D6StaticPreview(rotationX = 30f, rotationY = -30f)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Dice Faces Grid Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Dice Faces Grid Dark")
@Composable
fun D6PreviewDiceGrid() {
    DiceRollerTheme {
        Column {
            Row {
                D6StaticPreview(rotationX = CubeFace.FRONT.rotationX, rotationY = CubeFace.FRONT.rotationY)
                D6StaticPreview(rotationX = CubeFace.BACK.rotationX, rotationY = CubeFace.BACK.rotationY)
            }
            Row {
                D6StaticPreview(rotationX = CubeFace.TOP.rotationX, rotationY = CubeFace.TOP.rotationY)
                D6StaticPreview(rotationX = CubeFace.BOTTOM.rotationX, rotationY = CubeFace.BOTTOM.rotationY)
            }
            Row {
                D6StaticPreview(rotationX = CubeFace.LEFT.rotationX, rotationY = CubeFace.LEFT.rotationY)
                D6StaticPreview(rotationX = CubeFace.RIGHT.rotationX, rotationY = CubeFace.RIGHT.rotationY)
            }
        }
    }
}
