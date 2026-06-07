package com.greenfodor.diceroller.ui.dice.d10

import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withSave
import com.greenfodor.diceroller.geometry.PentagonalTrapezohedronGeometry
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

class D10Paints {
    val fillPaint = NativePaint().apply { isAntiAlias = true }
    val strokePaint = NativePaint().apply { isAntiAlias = true }
    val textPaint = NativePaint().apply {
        textAlign = NativePaint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }
    val nativeFacePath = NativePath()
    val dstArray = FloatArray(6)
    val rotatedVertices = ArrayList<Point3D>(PentagonalTrapezohedronGeometry.vertices.size).apply {
        repeat(PentagonalTrapezohedronGeometry.vertices.size) { add(Point3D(0f, 0f, 0f)) }
    }
    val projectedVertices = ArrayList<Point2D>(PentagonalTrapezohedronGeometry.vertices.size).apply {
        repeat(PentagonalTrapezohedronGeometry.vertices.size) { add(Point2D(0f, 0f)) }
    }
}

fun DrawScope.drawD10(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D10Paints,
    color: Color,
    labelFor: (geometryValue: Int) -> String = { it.toString() }
) {
    calculateGeometry(size, centerX, centerY, rotationX, rotationY, rotationZ, paints)
    val visibleFaces = getVisibleAndSortedFaces(color, paints.rotatedVertices, labelFor)
    visibleFaces.forEach { (face, normal, _) ->
        renderD10Face(face, normal, paints.projectedVertices, paints)
    }
}

private fun calculateGeometry(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D10Paints
) {
    val scaleFactor = size / 2f
    PentagonalTrapezohedronGeometry.vertices.forEachIndexed { index, baseV ->
        val v = baseV * scaleFactor
        val rotated = v.rotatePoint(rotationX, rotationY, rotationZ)
        paints.rotatedVertices[index] = rotated
        paints.projectedVertices[index] = rotated.projectPoint(centerX, centerY)
    }
}

private fun getVisibleAndSortedFaces(
    color: Color,
    rotatedVertices: List<Point3D>,
    labelFor: (geometryValue: Int) -> String
): List<Triple<PolyhedronFace, Point3D, Double>> {
    val faces = PentagonalTrapezohedronGeometry.faces.map { face ->
        PolyhedronFace(face.vertexIndices, color, labelFor(face.value))
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

private fun DrawScope.renderD10Face(
    face: PolyhedronFace,
    normal: Point3D,
    projectedVertices: List<Point2D>,
    paints: D10Paints
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
    paints: D10Paints
) {
    canvas.nativeCanvas.withSave {
        val matrix = Matrix()

        // Both upper and lower faces use their apex (vIndices[0]) as the top reference,
        // with the two wing vertices (vIndices[1]=right, vIndices[3]=left) completing the triangle.
        paints.dstArray[0] = projectedVertices[vIndices[0]].x
        paints.dstArray[1] = projectedVertices[vIndices[0]].y
        paints.dstArray[2] = projectedVertices[vIndices[1]].x
        paints.dstArray[3] = projectedVertices[vIndices[1]].y
        paints.dstArray[4] = projectedVertices[vIndices[3]].x
        paints.dstArray[5] = projectedVertices[vIndices[3]].y

        matrix.setPolyToPoly(DiceConstants.D20_SRC_TRIANGLE, 0, paints.dstArray, 0, 3)
        concat(matrix)

        paints.textPaint.apply {
            color = Color.White.copy(alpha = DiceConstants.D20_FACE_ALPHA).toArgb()
            textSize = DiceConstants.D20_TEXT_SIZE_UV
        }

        val baseline = -(paints.textPaint.descent() + paints.textPaint.ascent()) / 2 +
            DiceConstants.D10_TEXT_BASELINE_ADJUSTMENT

        drawText(label, 0f, baseline, paints.textPaint)

        if (label in DiceConstants.D20_AMBIGUOUS_LABELS) {
            val underlineLeft = -DiceConstants.D10_UNDERLINE_WIDTH_UV / 2f
            val underlineTop = baseline + DiceConstants.D10_UNDERLINE_TOP_OFFSET_UV
            drawRect(
                RectF(
                    underlineLeft,
                    underlineTop,
                    underlineLeft + DiceConstants.D10_UNDERLINE_WIDTH_UV,
                    underlineTop + DiceConstants.D10_UNDERLINE_HEIGHT_UV
                ),
                paints.textPaint
            )
        }
    }
}
