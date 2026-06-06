package com.greenfodor.diceroller.ui.dice.d20

import android.graphics.Matrix
import android.graphics.Typeface
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withSave
import com.greenfodor.diceroller.geometry.IcosahedronGeometry
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.calculateNormalZ
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.geometry.rotatePoint
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.DiceConstants.LIGHT_SOURCE
import com.greenfodor.diceroller.ui.dice.PolyhedronFace
import com.greenfodor.diceroller.ui.utils.shade
import android.graphics.Paint as NativePaint
import android.graphics.Path as NativePath

/**
 * Holds reusable [NativePaint] objects and temporary buffers used for D20 face rendering.
 */
class D20Paints {
    /** Fill paint for the face surface. */
    val fillPaint = NativePaint().apply { isAntiAlias = true }

    /** Stroke paint for face borders. */
    val strokePaint = NativePaint().apply { isAntiAlias = true }

    /** Native Paint for number rendering with high-quality antialiasing. */
    val textPaint = NativePaint().apply {
        textAlign = NativePaint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    /** Reusable native path for face polygon geometry. */
    val nativeFacePath = NativePath()

    /** Temporary buffer for matrix destination coordinates to avoid allocations. */
    val dstArray = FloatArray(6)

    /** Pre-allocated vertex buffers used to avoid per-frame allocations during the rotation loop. */
    val rotatedVertices = ArrayList<Point3D>(IcosahedronGeometry.vertices.size).apply {
        repeat(IcosahedronGeometry.vertices.size) { add(Point3D(0f, 0f, 0f)) }
    }

    /** Pre-allocated projection buffers used to avoid per-frame allocations during the 2D mapping loop. */
    val projectedVertices = ArrayList<Point2D>(IcosahedronGeometry.vertices.size).apply {
        repeat(IcosahedronGeometry.vertices.size) { add(Point2D(0f, 0f)) }
    }
}

/**
 * Performs the full 3D D20 (icosahedron) draw onto the canvas.
 *
 * @param size Outer diameter of the die in pixels.
 * @param centerX Horizontal center for projection.
 * @param centerY Vertical center for projection.
 * @param rotationX Current X rotation in degrees.
 * @param rotationY Current Y rotation in degrees.
 * @param rotationZ Current Z rotation (roll) in degrees.
 * @param paints Reusable [D20Paints] for style and buffers.
 * @param color The base theme color for the die.
 */
fun DrawScope.drawD20(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D20Paints,
    color: Color
) {
    calculateGeometry(
        size = size,
        centerX = centerX,
        centerY = centerY,
        rotationX = rotationX,
        rotationY = rotationY,
        rotationZ = rotationZ,
        paints = paints
    )

    val visibleFaces = getVisibleAndSortedFaces(
        color = color,
        rotatedVertices = paints.rotatedVertices
    )

    visibleFaces.forEach { (face, normal, _) ->
        renderD20Face(
            face = face,
            normal = normal,
            projectedVertices = paints.projectedVertices,
            paints = paints
        )
    }
}

private fun calculateGeometry(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D20Paints
) {
    val scaleFactor = size / 2f

    IcosahedronGeometry.vertices.forEachIndexed { index, baseV ->
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
    val faces = IcosahedronGeometry.faces.map { face ->
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

private fun DrawScope.renderD20Face(
    face: PolyhedronFace,
    normal: Point3D,
    projectedVertices: List<Point2D>,
    paints: D20Paints
) {
    val intensity = normal.dot(LIGHT_SOURCE).coerceIn(
        DiceConstants.MIN_SHADING_INTENSITY,
        DiceConstants.MAX_SHADING_INTENSITY
    )
    val shadedColor = face.baseColor.shade(intensity)
    val verts = face.vertexIndices.map { projectedVertices[it] }

    paints.nativeFacePath.rewind()
    paints.nativeFacePath.moveTo(verts[0].x, verts[0].y)
    for (i in 1 until verts.size) paints.nativeFacePath.lineTo(verts[i].x, verts[i].y)
    paints.nativeFacePath.close()

    drawIntoCanvas { canvas ->
        paints.fillPaint.apply {
            color = shadedColor.toArgb()
            style = NativePaint.Style.FILL
            pathEffect = null
        }
        canvas.nativeCanvas.drawPath(paints.nativeFacePath, paints.fillPaint)
        drawFaceLabel(canvas, face.label, face.vertexIndices, projectedVertices, paints)
        paints.strokePaint.apply {
            color = Color.White.copy(alpha = DiceConstants.D20_STROKE_ALPHA).toArgb()
            style = NativePaint.Style.STROKE
            strokeWidth = DiceConstants.STROKE_WIDTH
            pathEffect = null
        }
        canvas.nativeCanvas.drawPath(paints.nativeFacePath, paints.strokePaint)
    }
}

private fun drawFaceLabel(
    canvas: Canvas,
    label: String,
    vIndices: List<Int>,
    projectedVertices: List<Point2D>,
    paints: D20Paints
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

        if (label in DiceConstants.D20_AMBIGUOUS_LABELS) {
            val textY = -(paints.textPaint.descent() + paints.textPaint.ascent()) / 2 +
                DiceConstants.D20_TEXT_BASELINE_ADJUSTMENT
            val underlineY = textY + paints.textPaint.descent() + DiceConstants.D20_UNDERLINE_TOP_OFFSET_UV

            drawRect(
                -DiceConstants.D20_UNDERLINE_WIDTH_UV / 2,
                underlineY,
                DiceConstants.D20_UNDERLINE_WIDTH_UV / 2,
                underlineY + DiceConstants.D20_UNDERLINE_HEIGHT_UV,
                paints.textPaint
            )
        }
    }
}
