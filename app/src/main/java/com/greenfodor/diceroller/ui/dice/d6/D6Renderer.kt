package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.greenfodor.diceroller.geometry.HexahedronGeometry
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.calculateNormalZ
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.geometry.rotatePoint
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.DiceConstants.LIGHT_SOURCE
import com.greenfodor.diceroller.ui.theme.DiceColors
import com.greenfodor.diceroller.ui.utils.shade
import android.graphics.Paint as NativePaint
import android.graphics.Path as NativePath

/**
 * Holds reusable [NativePaint] and [Path] objects used for cube face rendering.
 *
 * Instantiating these once and reusing them across frames prevents unnecessary object allocation
 * and garbage collection during the animation loop.
 */
class D6Paints {
    /** Fill paint for the face surface. */
    val fillPaint = NativePaint().apply { isAntiAlias = true }

    /** Stroke paint for face borders. */
    val strokePaint = NativePaint().apply { isAntiAlias = true }

    /** Reusable native path for face polygon drawing. */
    val nativeFacePath = NativePath()

    /** Reusable Compose path for pip clipping. */
    val facePath = Path()

    /** Reusable path for pip geometry. */
    val dotPath = Path()

    /** Pre-allocated vertex buffers used to avoid per-frame allocations during the rotation loop. */
    val rotatedVertices = ArrayList<Point3D>(HexahedronGeometry.vertices.size).apply {
        repeat(HexahedronGeometry.vertices.size) { add(Point3D(0f, 0f, 0f)) }
    }

    /** Pre-allocated projection buffers used to avoid per-frame allocations during the 2D mapping loop. */
    val projectedVertices = ArrayList<Point2D>(HexahedronGeometry.vertices.size).apply {
        repeat(HexahedronGeometry.vertices.size) { add(Point2D(0f, 0f)) }
    }
}

/**
 * Performs the full 3D cube draw onto the canvas.
 *
 * This function handles the entire 3D-to-2D pipeline:
 * 1. **Geometry Calculation**: Scales, rotates, and projects the 8 vertices of the cube in a
 *    single pass to minimize list allocations.
 * 2. **Back-face Culling**: Calculates the surface normal for each face and skips rendering
 *    those pointing away from the camera (at most 3 faces are visible for a convex cube).
 * 3. **Painter's Algorithm Sorting**: Sorts the visible faces by their average Z-depth (back-to-front)
 *    to ensure correct overlapping.
 * 4. **Incremental Rendering**: Iterates through sorted faces to apply shading, pips, and strokes.
 *
 * @param size Side length of the cube in pixels.
 * @param centerX Horizontal center of the draw area.
 * @param centerY Vertical center of the draw area.
 * @param rotationX Current X-axis rotation in degrees.
 * @param rotationY Current Y-axis rotation in degrees.
 * @param rotationZ Current Z-axis rotation in degrees.
 * @param paints Reusable [D6Paints] to avoid per-frame allocations (includes path buffers).
 * @param diceColors Theme colors assigned to each face.
 */
fun DrawScope.drawD6(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D6Paints,
    diceColors: DiceColors
) {
    val halfSize = size / 2

    HexahedronGeometry.vertices.forEachIndexed { index, baseV ->
        val v = baseV * halfSize
        val rotated = v.rotatePoint(rotationX, rotationY, rotationZ)
        paints.rotatedVertices[index] = rotated
        paints.projectedVertices[index] = rotated.projectPoint(centerX, centerY)
    }

    val rotatedVertices = paints.rotatedVertices
    val projectedVertices = paints.projectedVertices

    val faces = createDiceFaceDescriptors(diceColors)

    val visibleFaces =
        faces
            .mapNotNull { face ->
                val vIndices = face.vertexIndices
                val v0 = rotatedVertices[vIndices[0]]
                val v1 = rotatedVertices[vIndices[1]]
                val v3 = rotatedVertices[vIndices[3]]

                val normalZ = calculateNormalZ(v0, v1, v3)

                if (normalZ > 0) {
                    val normal = (v1 - v0).cross(v3 - v0).normalize()
                    val avgDepth = vIndices.sumOf { rotatedVertices[it].z.toDouble() }
                    Triple(face, normal, avgDepth)
                } else {
                    null
                }
            }.sortedBy { it.third }

    visibleFaces.forEach { (face, normal, _) ->
        renderFace(
            face = face,
            normal = normal,
            rotatedVertices = rotatedVertices,
            projectedVertices = projectedVertices,
            centerX = centerX,
            centerY = centerY,
            paints = paints
        )
    }
}

/**
 * Maps [HexahedronGeometry.faces] to render descriptors by injecting the theme color for each value.
 *
 * @param diceColors The theme colors to apply to each face value.
 */
private fun createDiceFaceDescriptors(diceColors: DiceColors) =
    HexahedronGeometry.faces.map { face ->
        FaceDescriptor(
            vertexIndices = face.vertexIndices,
            baseColor = diceColors.colorForValue(face.value),
            dotCount = face.value
        )
    }

private fun DrawScope.renderFace(
    face: FaceDescriptor,
    normal: Point3D,
    rotatedVertices: List<Point3D>,
    projectedVertices: List<Point2D>,
    centerX: Float,
    centerY: Float,
    paints: D6Paints
) {
    val intensity =
        normal.dot(LIGHT_SOURCE).coerceIn(
            DiceConstants.MIN_SHADING_INTENSITY,
            DiceConstants.MAX_SHADING_INTENSITY
        )
    val shadedColor = face.baseColor.shade(intensity)
    val vIndices = face.vertexIndices
    val verts = vIndices.map { projectedVertices[it] }

    paints.nativeFacePath.rewind()
    paints.nativeFacePath.moveTo(verts[0].x, verts[0].y)
    for (i in 1 until verts.size) paints.nativeFacePath.lineTo(verts[i].x, verts[i].y)
    paints.nativeFacePath.close()

    paints.facePath.reset()
    paints.facePath.moveTo(verts[0].x, verts[0].y)
    for (i in 1 until verts.size) paints.facePath.lineTo(verts[i].x, verts[i].y)
    paints.facePath.close()

    drawIntoCanvas { canvas ->
        paints.fillPaint.apply {
            color = shadedColor.toArgb()
            style = NativePaint.Style.FILL
            pathEffect = null
        }
        canvas.nativeCanvas.drawPath(paints.nativeFacePath, paints.fillPaint)
    }

    val dotOffset = normal * DiceConstants.DOT_OFFSET_FACTOR
    clipPath(paints.facePath) {
        drawDiceDotsOnFace(
            dotCount = face.dotCount,
            vVertices = vIndices.map { rotatedVertices[it] },
            centerX = centerX,
            centerY = centerY,
            normalOffset = dotOffset,
            dotPath = paints.dotPath
        )
    }

    drawIntoCanvas { canvas ->
        paints.strokePaint.apply {
            color = Color.White.copy(alpha = DiceConstants.D6_STROKE_ALPHA).toArgb()
            style = NativePaint.Style.STROKE
            strokeWidth = DiceConstants.STROKE_WIDTH
            pathEffect = null
        }
        canvas.nativeCanvas.drawPath(paints.nativeFacePath, paints.strokePaint)
    }
}
