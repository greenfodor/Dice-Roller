package com.greenfodor.diceroller.ui.dice.d4

import android.graphics.Matrix
import android.graphics.Typeface
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withSave
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.TetrahedronGeometry
import com.greenfodor.diceroller.geometry.calculateNormalZ
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.geometry.rotatePoint
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.DiceConstants.LIGHT_SOURCE
import com.greenfodor.diceroller.ui.dice.PolyhedronFace
import com.greenfodor.diceroller.ui.utils.shade
import android.graphics.Paint as NativePaint

/**
 * Holds reusable [Paint] and temporary buffers used for D4 face rendering.
 */
class D4Paints {
    /** Paint for the main face surface (fill). */
    val face = Paint()

    /** Paint for the face borders (stroke). */
    val stroke = Paint()

    /** Native Paint for number rendering with high-quality antialiasing. */
    val textPaint = NativePaint().apply {
        textAlign = NativePaint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    /** Temporary buffer for matrix destination coordinates to avoid allocations. */
    val dstArray = FloatArray(6)

    /** Pre-allocated vertex buffers used to avoid per-frame allocations during the rotation loop. */
    val rotatedVertices = ArrayList<Point3D>(TetrahedronGeometry.vertices.size).apply {
        repeat(TetrahedronGeometry.vertices.size) { add(Point3D(0f, 0f, 0f)) }
    }

    /** Pre-allocated projection buffers used to avoid per-frame allocations during the 2D mapping loop. */
    val projectedVertices = ArrayList<Point2D>(TetrahedronGeometry.vertices.size).apply {
        repeat(TetrahedronGeometry.vertices.size) { add(Point2D(0f, 0f)) }
    }
}

/**
 * Performs the full 3D D4 (tetrahedron) draw onto the canvas.
 *
 * @param size Outer diameter of the die in pixels.
 * @param centerX Horizontal center for projection.
 * @param centerY Vertical center for projection.
 * @param rotationX Current X rotation in degrees.
 * @param rotationY Current Y rotation in degrees.
 * @param rotationZ Current Z rotation (roll) in degrees.
 * @param facePath Reusable [Path] for geometry.
 * @param paints Reusable [D4Paints] for style and buffers.
 * @param color The base theme color for the die.
 */
fun DrawScope.drawD4(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    facePath: Path,
    paints: D4Paints,
    color: Color
) {
    // 1. Geometry Calculation
    calculateGeometry(size, centerX, centerY, rotationX, rotationY, rotationZ, paints)

    // 2. Culling and Sorting
    val visibleFaces = getVisibleAndSortedFaces(color, paints.rotatedVertices)

    // 3. Rendering
    visibleFaces.forEach { (face, normal, _) ->
        renderD4Face(face, normal, paints.projectedVertices, facePath, paints)
    }
}

private fun calculateGeometry(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D4Paints
) {
    val scaleFactor = size / 2f

    TetrahedronGeometry.vertices.forEachIndexed { index, baseV ->
        val v = baseV * scaleFactor
        val rotated = v.rotatePoint(rotationX, rotationY, rotationZ)
        paints.rotatedVertices[index] = rotated
        paints.projectedVertices[index] = rotated.projectPoint(centerX, centerY)
    }
}

private fun getVisibleAndSortedFaces(
    color: Color,
    rotatedVertices: List<Point3D>
): List<Triple<PolyhedronFace, Point3D, Double>> {
    val faces = TetrahedronGeometry.faces.map { face ->
        PolyhedronFace(face.vertexIndices, color, face.value.toString())
    }

    return faces.mapNotNull { face ->
        val vIndices = face.vertexIndices
        val v0 = rotatedVertices[vIndices[0]]
        val v1 = rotatedVertices[vIndices[1]]
        val v2 = rotatedVertices[vIndices[2]]

        val normalZ = calculateNormalZ(v0, v1, v2)

        if (normalZ > 0) {
            val normal = (v1 - v0).cross(v2 - v0).normalize()
            val avgDepth = vIndices.sumOf { rotatedVertices[it].z.toDouble() }
            Triple(face, normal, avgDepth)
        } else {
            null
        }
    }.sortedBy { it.third }
}

private fun DrawScope.renderD4Face(
    face: PolyhedronFace,
    normal: Point3D,
    projectedVertices: List<Point2D>,
    facePath: Path,
    paints: D4Paints
) {
    val shadedColor = calculateShadedColor(face.baseColor, normal)

    buildFacePath(facePath, face.vertexIndices, projectedVertices)

    drawIntoCanvas { canvas ->
        drawFaceSurface(canvas, facePath, shadedColor, paints.face)
        drawFaceLabel(canvas, face.label, face.vertexIndices, projectedVertices, paints)
        drawFaceStroke(canvas, facePath, paints.stroke)
    }
}

private fun calculateShadedColor(baseColor: Color, normal: Point3D): Color {
    val intensity = normal.dot(LIGHT_SOURCE).coerceIn(
        DiceConstants.MIN_SHADING_INTENSITY,
        DiceConstants.MAX_SHADING_INTENSITY
    )
    return baseColor.shade(intensity)
}

private fun buildFacePath(path: Path, vertexIndices: List<Int>, projectedVertices: List<Point2D>) {
    path.reset()
    path.moveTo(projectedVertices[vertexIndices[0]].x, projectedVertices[vertexIndices[0]].y)
    for (i in 1 until vertexIndices.size) {
        path.lineTo(projectedVertices[vertexIndices[i]].x, projectedVertices[vertexIndices[i]].y)
    }
    path.close()
}

private fun drawFaceSurface(canvas: Canvas, path: Path, color: Color, paint: Paint) {
    paint.apply {
        this.color = color
        style = PaintingStyle.Fill
    }
    canvas.drawOutline(Outline.Generic(path), paint)
}

private fun drawFaceLabel(
    canvas: Canvas,
    label: String,
    vIndices: List<Int>,
    projectedVertices: List<Point2D>,
    paints: D4Paints
) {
    canvas.nativeCanvas.withSave {
        val matrix = Matrix()
        paints.dstArray[0] = projectedVertices[vIndices[0]].x
        paints.dstArray[1] = projectedVertices[vIndices[0]].y
        paints.dstArray[2] = projectedVertices[vIndices[1]].x
        paints.dstArray[3] = projectedVertices[vIndices[1]].y
        paints.dstArray[4] = projectedVertices[vIndices[2]].x
        paints.dstArray[5] = projectedVertices[vIndices[2]].y

        matrix.setPolyToPoly(DiceConstants.D20_SRC_TRIANGLE, 0, paints.dstArray, 0, 3)
        concat(matrix)

        paints.textPaint.apply {
            color = Color.White.copy(alpha = DiceConstants.D20_FACE_ALPHA).toArgb()
            textSize = DiceConstants.D20_TEXT_SIZE_UV
        }

        drawText(
            label,
            0f,
            -(paints.textPaint.descent() + paints.textPaint.ascent()) / 2 + DiceConstants.D20_TEXT_BASELINE_ADJUSTMENT,
            paints.textPaint
        )
    }
}

private fun drawFaceStroke(canvas: Canvas, path: Path, paint: Paint) {
    paint.apply {
        color = Color.White.copy(alpha = DiceConstants.D20_STROKE_ALPHA)
        style = PaintingStyle.Stroke
        strokeWidth = DiceConstants.STROKE_WIDTH
    }
    canvas.drawOutline(Outline.Generic(path), paint)
}
