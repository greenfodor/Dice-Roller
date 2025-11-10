package com.greenfodor.diceroller

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(val x: Float, val y: Float, val z: Float)

enum class CubeFace(val rotationX: Float, val rotationY: Float, val color: Color) {
    FRONT(0f, 0f, Color(0xFFFF6B6B)),
    BACK(0f, 180f, Color(0xFF4ECDC4)),
    TOP(270f, 0f, Color(0xFF95E1D3)),
    BOTTOM(90f, 0f, Color(0xFFFFE66D)),
    LEFT(0f, 270f, Color(0xFFA8E6CF)),
    RIGHT(0f, 90f, Color(0xFFDCCEFF))
}

@Composable
fun RollingCubeAnimation() {
    var targetFace by remember { mutableStateOf(CubeFace.FRONT) }
    var currentRotationX by remember { mutableStateOf(0f) }
    var currentRotationY by remember { mutableStateOf(0f) }
    var targetRotationX by remember { mutableStateOf(0f) }
    var targetRotationY by remember { mutableStateOf(0f) }

    val rotationX by animateFloatAsState(
        targetValue = targetRotationX,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            currentRotationX = it
        },
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = targetRotationY,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            currentRotationY = it
        },
        label = "rotationY"
    )

    val isRolling = rotationX != targetRotationX || rotationY != targetRotationY

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        ) {
            val cubeSize = 320f
            val centerX = size.width / 2
            val centerY = size.height / 2

            drawCube(
                size = cubeSize,
                centerX = centerX,
                centerY = centerY,
                rotationX = rotationX,
                rotationY = rotationY
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                targetFace = CubeFace.entries.random()
                targetRotationX = currentRotationX + targetFace.rotationX + 720f
                targetRotationY = currentRotationY + targetFace.rotationY + 900f
            },
            enabled = !isRolling
        ) {
            Text("Roll Cube (Random)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    targetFace = CubeFace.FRONT
                    targetRotationX = currentRotationX + targetFace.rotationX + 720f
                    targetRotationY = currentRotationY + targetFace.rotationY + 900f
                },
                enabled = !isRolling,
                modifier = Modifier.weight(1f)
            ) {
                Text("Front")
            }

            Button(
                onClick = {
                    targetFace = CubeFace.BACK
                    targetRotationX = currentRotationX + targetFace.rotationX + 720f
                    targetRotationY = currentRotationY + targetFace.rotationY + 900f
                },
                enabled = !isRolling,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    targetFace = CubeFace.TOP
                    targetRotationX = currentRotationX + targetFace.rotationX + 720f
                    targetRotationY = currentRotationY + targetFace.rotationY + 900f
                },
                enabled = !isRolling,
                modifier = Modifier.weight(1f)
            ) {
                Text("Top")
            }

            Button(
                onClick = {
                    targetFace = CubeFace.BOTTOM
                    targetRotationX = currentRotationX + targetFace.rotationX + 720f
                    targetRotationY = currentRotationY + targetFace.rotationY + 900f
                },
                enabled = !isRolling,
                modifier = Modifier.weight(1f)
            ) {
                Text("Bottom")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    targetFace = CubeFace.LEFT
                    targetRotationX = currentRotationX + targetFace.rotationX + 720f
                    targetRotationY = currentRotationY + targetFace.rotationY + 900f
                },
                enabled = !isRolling,
                modifier = Modifier.weight(1f)
            ) {
                Text("Left")
            }

            Button(
                onClick = {
                    targetFace = CubeFace.RIGHT
                    targetRotationX = currentRotationX + targetFace.rotationX + 720f
                    targetRotationY = currentRotationY + targetFace.rotationY + 900f
                },
                enabled = !isRolling,
                modifier = Modifier.weight(1f)
            ) {
                Text("Right")
            }
        }

        if (!isRolling) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Current face: ${targetFace.name}")
        }
    }
}

fun DrawScope.drawCube(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float
) {
    val halfSize = size / 2

    val vertices = listOf(
        Point3D(-halfSize, -halfSize, -halfSize),
        Point3D(halfSize, -halfSize, -halfSize),
        Point3D(halfSize, halfSize, -halfSize),
        Point3D(-halfSize, halfSize, -halfSize),
        Point3D(-halfSize, -halfSize, halfSize),
        Point3D(halfSize, -halfSize, halfSize),
        Point3D(halfSize, halfSize, halfSize),
        Point3D(-halfSize, halfSize, halfSize)
    )

    val rotatedVertices = vertices.map { vertex ->
        rotatePoint(vertex, rotationX, rotationY)
    }

    val projectedVertices = rotatedVertices.map { vertex ->
        projectPoint(vertex, centerX, centerY)
    }

    val faces = listOf(
        listOf(0, 1, 2, 3) to Color(0xFFFF6B6B),
        listOf(4, 5, 6, 7) to Color(0xFF4ECDC4),
        listOf(0, 1, 5, 4) to Color(0xFFFFE66D),
        listOf(2, 3, 7, 6) to Color(0xFF95E1D3),
        listOf(0, 3, 7, 4) to Color(0xFFA8E6CF),
        listOf(1, 2, 6, 5) to Color(0xFFDCCEFF)
    )

    val facesWithDepth = faces.map { (indices, color) ->
        val avgZ = indices.map { rotatedVertices[it].z }.average()
        Triple(indices, color, avgZ)
    }.sortedBy { it.third }

    // Corner radius for rounded edges - adjust this value to control roundness
    val cornerRadius = 20f

    facesWithDepth.forEach { (indices, color, _) ->
        val path = Path().apply {
            moveTo(projectedVertices[indices[0]].x, projectedVertices[indices[0]].y)
            for (i in 1 until indices.size) {
                lineTo(projectedVertices[indices[i]].x, projectedVertices[indices[i]].y)
            }
            close()
        }

        // Draw filled face with rounded corners
        drawIntoCanvas { canvas ->
            canvas.drawOutline(
                outline = Outline.Generic(path),
                paint = Paint().apply {
                    this.color = color.copy(alpha = 0.85f)
                    pathEffect = PathEffect.cornerPathEffect(cornerRadius)
                }
            )
        }

        // Draw stroke with rounded corners
        drawIntoCanvas { canvas ->
            canvas.drawOutline(
                outline = Outline.Generic(path),
                paint = Paint().apply {
                    this.color = Color.Black.copy(alpha = 0.3f)
                    style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                    strokeWidth = 2f
                    pathEffect = PathEffect.cornerPathEffect(cornerRadius)
                }
            )
        }
    }
}

fun rotatePoint(point: Point3D, angleX: Float, angleY: Float): Point3D {
    val radX = Math.toRadians(angleX.toDouble())
    val radY = Math.toRadians(angleY.toDouble())

    var x = point.x
    var y = point.y
    var z = point.z

    val cosY = cos(radY).toFloat()
    val sinY = sin(radY).toFloat()
    val x1 = x * cosY - z * sinY
    val z1 = x * sinY + z * cosY

    val cosX = cos(radX).toFloat()
    val sinX = sin(radX).toFloat()
    val y2 = y * cosX - z1 * sinX
    val z2 = y * sinX + z1 * cosX

    return Point3D(x1, y2, z2)
}

fun projectPoint(point: Point3D, centerX: Float, centerY: Float): Offset {
    val cameraDistance = 800f
    val fov = 500f

    val factor = fov / (cameraDistance - point.z)

    return Offset(
        centerX + point.x * factor,
        centerY + point.y * factor
    )
}
