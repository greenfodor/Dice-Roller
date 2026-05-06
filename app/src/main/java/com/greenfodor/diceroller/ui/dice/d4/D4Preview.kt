package com.greenfodor.diceroller.ui.dice.d4

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

@Composable
private fun D4StaticPreview(
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    modifier: Modifier = Modifier
) {
    val facePath = remember { Path() }
    val paints = remember { D4Paints() }
    val color = MaterialTheme.colorScheme.tertiary

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
            drawD4(
                size = DiceConstants.DEFAULT_CUBE_SIZE * 0.6f,
                centerX = size.width / 2,
                centerY = size.height / 2,
                rotationX = rotationX,
                rotationY = rotationY,
                rotationZ = rotationZ,
                facePath = facePath,
                paints = paints,
                color = color
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Face 1 Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Face 1 Dark")
@Composable
fun D4PreviewFace1() {
    DiceRollerTheme {
        val face = D4.faces[0]
        D4StaticPreview(rotationX = face.rotationX, rotationY = face.rotationY, rotationZ = face.rotationZ)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Face 4 Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Face 4 Dark")
@Composable
fun D4PreviewFace4() {
    DiceRollerTheme {
        val face = D4.faces[3]
        D4StaticPreview(rotationX = face.rotationX, rotationY = face.rotationY, rotationZ = face.rotationZ)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Angled View Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Angled View Dark")
@Composable
fun D4PreviewAngled() {
    DiceRollerTheme {
        D4StaticPreview(rotationX = 30f, rotationY = 45f, rotationZ = 0f)
    }
}
