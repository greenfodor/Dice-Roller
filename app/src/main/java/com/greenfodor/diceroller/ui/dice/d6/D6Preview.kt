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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.LocalDiceColors

@Composable
private fun D6StaticPreview(
    rotationX: Float,
    rotationY: Float,
    modifier: Modifier = Modifier,
    rotationZ: Float = 0f
) {
    val diceColors = LocalDiceColors.current
    val paints = remember { D6Paints() }

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
            drawD6(
                size = DiceConstants.DEFAULT_CUBE_SIZE * 0.6f,
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
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Front Face Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Front Face Dark")
@Composable
fun D6PreviewFront() {
    DiceRollerTheme {
        val face = D6.faces[0]
        D6StaticPreview(rotationX = face.rotationX, rotationY = face.rotationY, rotationZ = face.rotationZ)
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
                val f1 = D6.faces[0]
                val f6 = D6.faces[5]
                D6StaticPreview(rotationX = f1.rotationX, rotationY = f1.rotationY, rotationZ = f1.rotationZ)
                D6StaticPreview(rotationX = f6.rotationX, rotationY = f6.rotationY, rotationZ = f6.rotationZ)
            }
            Row {
                val f5 = D6.faces[4]
                val f2 = D6.faces[1]
                D6StaticPreview(rotationX = f5.rotationX, rotationY = f5.rotationY, rotationZ = f5.rotationZ)
                D6StaticPreview(rotationX = f2.rotationX, rotationY = f2.rotationY, rotationZ = f2.rotationZ)
            }
            Row {
                val f4 = D6.faces[3]
                val f3 = D6.faces[2]
                D6StaticPreview(rotationX = f4.rotationX, rotationY = f4.rotationY, rotationZ = f4.rotationZ)
                D6StaticPreview(rotationX = f3.rotationX, rotationY = f3.rotationY, rotationZ = f3.rotationZ)
            }
        }
    }
}
