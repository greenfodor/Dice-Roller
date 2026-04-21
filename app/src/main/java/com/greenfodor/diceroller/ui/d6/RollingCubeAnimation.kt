package com.greenfodor.diceroller.ui.d6

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.geometry.*
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceColors
import com.greenfodor.diceroller.ui.theme.LocalDiceColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RollingCubeAnimation() {
    val diceColors = LocalDiceColors.current
    var targetFace by remember { mutableStateOf(CubeFace.FRONT) }
    var currentRotationX by remember { mutableFloatStateOf(0f) }
    var currentRotationY by remember { mutableFloatStateOf(0f) }
    var targetRotationX by remember { mutableFloatStateOf(0f) }
    var targetRotationY by remember { mutableFloatStateOf(0f) }

    val facePath = remember { Path() }
    val dotPath = remember { Path() }
    val facePaint = remember { Paint() }
    val strokePaint = remember { Paint() }

    val rotationX by animateFloatAsState(
        targetValue = targetRotationX,
        animationSpec = tween(
            durationMillis = DiceConstants.ROLL_DURATION_MILLIS,
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
            durationMillis = DiceConstants.ROLL_DURATION_MILLIS,
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
                .graphicsLayer {
                    clip = false
                }
        ) {
            val cubeSize = DiceConstants.DEFAULT_CUBE_SIZE
            val centerX = size.width / 2
            val centerY = size.height / 2

            drawCube(
                size = cubeSize,
                centerX = centerX,
                centerY = centerY,
                rotationX = rotationX,
                rotationY = rotationY,
                facePath = facePath,
                dotPath = dotPath,
                facePaint = facePaint,
                strokePaint = strokePaint,
                diceColors = diceColors
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                targetFace = CubeFace.entries.random()
                targetRotationX = currentRotationX + targetFace.rotationX + DiceConstants.ROTATION_X_OFFSET
                targetRotationY = currentRotationY + targetFace.rotationY + DiceConstants.ROTATION_Y_OFFSET
            },
            enabled = !isRolling
        ) {
            Text("Roll Cube")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Performs the drawing of a 3D cube onto the canvas.
 * This function handles vertex rotation, perspective projection, depth sorting,
 * shading, and face rendering.
 *
 * @param size The size of the cube in pixels.
 * @param centerX The horizontal center of the drawing area.
 * @param centerY The vertical center of the drawing area.
 * @param rotationX Current rotation around the X axis.
 * @param rotationY Current rotation around the Y axis.
 * @param facePath Reused [Path] object for face geometry.
 * @param dotPath Reused [Path] object for dot geometry.
 * @param facePaint Reused [Paint] object for face filling.
 * @param strokePaint Reused [Paint] object for face outlines.
 */
fun DrawScope.drawCube(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    facePath: Path,
    dotPath: Path,
    facePaint: Paint,
    strokePaint: Paint,
    diceColors: DiceColors
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

    // Standard dice faces: opposite sides sum to 7
    val faces = listOf(
        Triple(listOf(4, 5, 6, 7), diceColors.face1, 1),  // Front (Z+) - 1 dot
        Triple(listOf(0, 1, 2, 3), diceColors.face6, 6),  // Back (Z-) - 6 dots
        Triple(listOf(0, 1, 5, 4), diceColors.face2, 2),  // Bottom (Y-) - 2 dots
        Triple(listOf(2, 3, 7, 6), diceColors.face5, 5),  // Top (Y+) - 5 dots
        Triple(listOf(0, 3, 7, 4), diceColors.face4, 4),  // Left (X-) - 4 dots
        Triple(listOf(1, 2, 6, 5), diceColors.face3, 3)   // Right (X+) - 3 dots
    )

    // Sort faces by average depth (Painter's Algorithm)
    val facesWithDepth = faces.map { (indices, color, dots) ->
        val avgZ = indices.map { rotatedVertices[it].z }.average()
        Pair(Triple(indices, color, dots), avgZ)
    }.sortedBy { it.second }

    val cornerRadius = DiceConstants.CORNER_RADIUS
    val lightSource = Point3D(0.5f, -1f, 1.5f).normalize()
    val pathEffect = PathEffect.cornerPathEffect(cornerRadius)

    facesWithDepth.forEach { (faceData, _) ->
        val (indices, color, dotCount) = faceData

        val v0 = rotatedVertices[indices[0]]
        val v1 = rotatedVertices[indices[1]]
        val v3 = rotatedVertices[indices[3]]
        val normal = (v1 - v0).cross(v3 - v0).normalize()

        val intensity = normal.dot(lightSource).coerceIn(
            DiceConstants.MIN_SHADING_INTENSITY,
            DiceConstants.MAX_SHADING_INTENSITY
        )
        val shadedColor = Color(
            red = color.red * intensity,
            green = color.green * intensity,
            blue = color.blue * intensity,
            alpha = color.alpha
        )

        facePath.reset()
        facePath.moveTo(projectedVertices[indices[0]].x, projectedVertices[indices[0]].y)
        for (i in 1 until indices.size) {
            facePath.lineTo(projectedVertices[indices[i]].x, projectedVertices[indices[i]].y)
        }
        facePath.close()

        // Tiny offset to prevent "Z-fighting" with the face plane
        val dotOffset = normal * DiceConstants.DOT_OFFSET_FACTOR

        drawIntoCanvas { canvas ->
            facePaint.apply {
                this.color = shadedColor
                this.pathEffect = pathEffect
                this.style = PaintingStyle.Fill
            }
            canvas.drawOutline(
                outline = Outline.Generic(facePath),
                paint = facePaint
            )
        }

        clipPath(facePath) {
            drawDiceDotsOnFace(
                dotCount = dotCount,
                v0 = rotatedVertices[indices[0]],
                v1 = rotatedVertices[indices[1]],
                v2 = rotatedVertices[indices[2]],
                v3 = rotatedVertices[indices[3]],
                centerX = centerX,
                centerY = centerY,
                normalOffset = dotOffset,
                dotPath = dotPath
            )
        }

        drawIntoCanvas { canvas ->
            strokePaint.apply {
                this.color = Color.White.copy(alpha = 0.5f)
                this.style = PaintingStyle.Stroke
                this.strokeWidth = DiceConstants.STROKE_WIDTH
                this.pathEffect = pathEffect
            }
            canvas.drawOutline(
                outline = Outline.Generic(facePath),
                paint = strokePaint
            )
        }
    }
}

/**
 * Draws the pips (dots) on a specific face of the dice.
 * Dots are rendered as flat 3D circles using polygonal approximation to
 * ensure they distort correctly with the face's perspective.
 *
 * @param dotCount Number of dots to draw (1 to 6).
 * @param v0 The first vertex of the face.
 * @param v1 The second vertex of the face.
 * @param v2 The third vertex of the face.
 * @param v3 The fourth vertex of the face.
 * @param normalOffset A small vector used to lift dots slightly above the face.
 * @param dotPath Reused [Path] object for dot geometry.
 */
private fun DrawScope.drawDiceDotsOnFace(
    dotCount: Int,
    v0: Point3D,
    v1: Point3D,
    v2: Point3D,
    v3: Point3D,
    centerX: Float,
    centerY: Float,
    normalOffset: Point3D,
    dotPath: Path
) {
    // Helper function to get a point on the face using normalized coordinates
    fun getPoint3DOnFace(u: Float, v: Float): Point3D {
        val s = (u + 1f) / 2f
        val t = (v + 1f) / 2f
        return Point3D(
            (1 - s) * (1 - t) * v0.x + s * (1 - t) * v1.x + s * t * v2.x + (1 - s) * t * v3.x,
            (1 - s) * (1 - t) * v0.y + s * (1 - t) * v1.y + s * t * v2.y + (1 - s) * t * v3.y,
            (1 - s) * (1 - t) * v0.z + s * (1 - t) * v1.z + s * t * v2.z + (1 - s) * t * v3.z
        ) + normalOffset
    }

    val dotRadiusFactor = DiceConstants.DOT_RADIUS_FACTOR
    val spacing = DiceConstants.DOT_SPACING_FACTOR
    val dotColor = Color.White.copy(alpha = 0.9f)

    val dotCenters = when (dotCount) {
        1 -> listOf(Offset(0f, 0f))
        2 -> listOf(Offset(-spacing, spacing), Offset(spacing, -spacing))
        3 -> listOf(Offset(-spacing, spacing), Offset(0f, 0f), Offset(spacing, -spacing))
        4 -> listOf(
            Offset(-spacing, -spacing), Offset(spacing, -spacing),
            Offset(-spacing, spacing), Offset(spacing, spacing)
        )
        5 -> listOf(
            Offset(-spacing, -spacing), Offset(spacing, -spacing), Offset(0f, 0f),
            Offset(-spacing, spacing), Offset(spacing, spacing)
        )
        6 -> listOf(
            Offset(-spacing, -spacing), Offset(-spacing, 0f), Offset(-spacing, spacing),
            Offset(spacing, -spacing), Offset(spacing, 0f), Offset(spacing, spacing)
        )
        else -> emptyList()
    }

    dotCenters.forEach { center ->
        dotPath.reset()
        val segments = DiceConstants.DOT_SEGMENTS
        for (i in 0 until segments) {
            val angle = 2.0 * Math.PI * i / segments
            val u = center.x + cos(angle).toFloat() * dotRadiusFactor
            val v = center.y + sin(angle).toFloat() * dotRadiusFactor
            
            val point3D = getPoint3DOnFace(u, v)
            val projected = projectPoint(point3D, centerX, centerY)
            
            if (i == 0) dotPath.moveTo(projected.x, projected.y)
            else dotPath.lineTo(projected.x, projected.y)
        }
        dotPath.close()
        drawPath(path = dotPath, color = dotColor)
    }
}
