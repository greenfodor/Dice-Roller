package com.greenfodor.diceroller.ui.dice.d20

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
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
private fun D20StaticPreview(
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    modifier: Modifier = Modifier
) {
    val facePath = remember { Path() }
    val paints = remember { D20Paints() }
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier =
            modifier
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
            drawD20(
                size = DiceConstants.DEFAULT_CUBE_SIZE * 0.6f,
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
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Face 1 Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Face 1 Dark")
@Composable
fun D20PreviewFace1() {
    DiceRollerTheme {
        val face = D20.faces[0]
        D20StaticPreview(rotationX = face.rotationX, rotationY = face.rotationY, rotationZ = face.rotationZ)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Face 20 Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Face 20 Dark")
@Composable
fun D20PreviewFace20() {
    DiceRollerTheme {
        val face = D20.faces[19]
        D20StaticPreview(rotationX = face.rotationX, rotationY = face.rotationY, rotationZ = face.rotationZ)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Angled View Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Angled View Dark")
@Composable
fun D20PreviewAngled() {
    DiceRollerTheme {
        D20StaticPreview(rotationX = 45f, rotationY = 45f, rotationZ = 0f)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "D20 Faces Grid Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "D20 Faces Grid Dark")
@Composable
fun D20PreviewGrid() {
    DiceRollerTheme {
        Column {
            Row {
                D20StaticPreview(
                    rotationX = D20.faces[0].rotationX,
                    rotationY = D20.faces[0].rotationY,
                    rotationZ = D20.faces[0].rotationZ
                )
                D20StaticPreview(
                    rotationX = D20.faces[1].rotationX,
                    rotationY = D20.faces[1].rotationY,
                    rotationZ = D20.faces[1].rotationZ
                )
                D20StaticPreview(
                    rotationX = D20.faces[2].rotationX,
                    rotationY = D20.faces[2].rotationY,
                    rotationZ = D20.faces[2].rotationZ
                )
            }
            Row {
                D20StaticPreview(
                    rotationX = D20.faces[3].rotationX,
                    rotationY = D20.faces[3].rotationY,
                    rotationZ = D20.faces[3].rotationZ
                )
                D20StaticPreview(
                    rotationX = D20.faces[4].rotationX,
                    rotationY = D20.faces[4].rotationY,
                    rotationZ = D20.faces[4].rotationZ
                )
                D20StaticPreview(
                    rotationX = D20.faces[5].rotationX,
                    rotationY = D20.faces[5].rotationY,
                    rotationZ = D20.faces[5].rotationZ
                )
            }
        }
    }
}
