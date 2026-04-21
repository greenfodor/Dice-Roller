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
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Point3D(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Point3D(x * scalar, y * scalar, z * scalar)

    fun normalize(): Point3D {
        val len = sqrt(x * x + y * y + z * z)
        return if (len > 0) Point3D(x / len, y / len, z / len) else this
    }

    fun cross(other: Point3D) = Point3D(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
}

enum class CubeFace(val rotationX: Float, val rotationY: Float, val dots: Int) {
    FRONT(0f, 0f, 1),
    BACK(0f, 180f, 6),
    TOP(270f, 0f, 5),
    BOTTOM(90f, 0f, 2),
    LEFT(0f, 270f, 4),
    RIGHT(0f, 90f, 3)
}

@Composable
fun RollingCubeAnimation() {
    var targetFace by remember { mutableStateOf(CubeFace.FRONT) }
    var currentRotationX by remember { mutableFloatStateOf(0f) }
    var currentRotationY by remember { mutableFloatStateOf(0f) }
    var targetRotationX by remember { mutableFloatStateOf(0f) }
    var targetRotationY by remember { mutableFloatStateOf(0f) }

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
            Text("Roll Cube")
        }

        Spacer(modifier = Modifier.height(16.dp))
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

    // Define faces with their dot counts
    val faces = listOf(
        Triple(listOf(0, 1, 2, 3), Color(0xFFFF6B6B), 1),  // Front - 1 dot
        Triple(listOf(4, 5, 6, 7), Color(0xFF4ECDC4), 6),  // Back - 6 dots
        Triple(listOf(0, 1, 5, 4), Color(0xFFFFE66D), 2),  // Bottom - 2 dots
        Triple(listOf(2, 3, 7, 6), Color(0xFF95E1D3), 5),  // Top - 5 dots
        Triple(listOf(0, 3, 7, 4), Color(0xFFA8E6CF), 4),  // Left - 4 dots
        Triple(listOf(1, 2, 6, 5), Color(0xFFDCCEFF), 3)   // Right - 3 dots
    )

    val facesWithDepth = faces.map { (indices, color, dots) ->
        val avgZ = indices.map { rotatedVertices[it].z }.average()
        Pair(Triple(indices, color, dots), avgZ)
    }.sortedBy { it.second }

    val cornerRadius = 20f

    // Draw each face with its dots
    facesWithDepth.forEach { (faceData, _) ->
        val (indices, color, dotCount) = faceData

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
                    this.color = color
                    pathEffect = PathEffect.cornerPathEffect(cornerRadius)
                }
            )
        }

        // Calculate face normal for offset
        val v0 = rotatedVertices[indices[0]]
        val v1 = rotatedVertices[indices[1]]
        val v2 = rotatedVertices[indices[3]]

        // Calculate two edge vectors
        val edge1 = v1 - v0
        val edge2 = v2 - v0

        // Cross product gives us the face normal
        val normal = edge1.cross(edge2).normalize()

        // Small offset to lift dots slightly above the face (prevents z-fighting)
        val offset = normal * 0.5f

        // Create a clipping path for the face (with rounded corners)
        val clipPath = Path().apply {
            moveTo(projectedVertices[indices[0]].x, projectedVertices[indices[0]].y)
            for (i in 1 until indices.size) {
                lineTo(projectedVertices[indices[i]].x, projectedVertices[indices[i]].y)
            }
            close()
        }

        // Draw dots on this face with clipping applied
        clipPath(clipPath) {
            drawDiceDotsOnFace(
                dotCount = dotCount,
                v0 = rotatedVertices[indices[0]],
                v1 = rotatedVertices[indices[1]],
                v2 = rotatedVertices[indices[2]],
                v3 = rotatedVertices[indices[3]],
                centerX = centerX,
                centerY = centerY,
                normalOffset = offset
            )
        }

        // Draw stroke with rounded corners on top
        drawIntoCanvas { canvas ->
            canvas.drawOutline(
                outline = Outline.Generic(path),
                paint = Paint().apply {
                    this.color = Color.White.copy(alpha = 0.8f)
                    style = PaintingStyle.Stroke
                    strokeWidth = 4f
                    pathEffect = PathEffect.cornerPathEffect(cornerRadius)
                }
            )
        }
    }
}

fun DrawScope.drawDiceDotsOnFace(
    dotCount: Int,
    v0: Point3D,
    v1: Point3D,
    v2: Point3D,
    v3: Point3D,
    centerX: Float,
    centerY: Float,
    normalOffset: Point3D
) {
    // Helper function to get a point on the face using normalized coordinates
    // u and v range from -1 to 1, representing positions on the face
    fun getPointOnFace(u: Float, v: Float): Offset {
        // Convert from [-1, 1] to [0, 1] for interpolation
        val s = (u + 1f) / 2f
        val t = (v + 1f) / 2f

        // Bilinear interpolation on the quad face with normal offset
        val point = Point3D(
            (1 - s) * (1 - t) * v0.x + s * (1 - t) * v1.x + s * t * v2.x + (1 - s) * t * v3.x,
            (1 - s) * (1 - t) * v0.y + s * (1 - t) * v1.y + s * t * v2.y + (1 - s) * t * v3.y,
            (1 - s) * (1 - t) * v0.z + s * (1 - t) * v1.z + s * t * v2.z + (1 - s) * t * v3.z
        ) + normalOffset  // Add small offset along face normal

        return projectPoint(point, centerX, centerY)
    }

    val dotRadius = 16f
    val spacing = 0.5f  // Position offset in normalized coordinates
    val dotColor = Color.White

    when (dotCount) {
        1 -> {
            // Center dot
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(0f, 0f))
        }
        2 -> {
            // Diagonal dots (top-left to bottom-right)
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, -spacing))
        }
        3 -> {
            // Diagonal dots with center
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(0f, 0f))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, -spacing))
        }
        4 -> {
            // Four corners
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, -spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, -spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, spacing))
        }
        5 -> {
            // Four corners + center
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, -spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, -spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(0f, 0f))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, spacing))
        }
        6 -> {
            // Two columns of three
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, -spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, 0f))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(-spacing, spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, -spacing))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, 0f))
            drawCircle(color = dotColor, radius = dotRadius, center = getPointOnFace(spacing, spacing))
        }
    }
}

fun rotatePoint(point: Point3D, angleX: Float, angleY: Float): Point3D {
    val radX = Math.toRadians(angleX.toDouble())
    val radY = Math.toRadians(angleY.toDouble())

    val x = point.x
    val y = point.y
    val z = point.z

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
