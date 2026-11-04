package com.greenfodor.diceroller.ui.dice.d10

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import com.greenfodor.diceroller.geometry.AFFINE_COEFFICIENT_COUNT
import com.greenfodor.diceroller.geometry.PentagonalTrapezohedronGeometry
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.calculateNormalZ
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.geometry.rotatePoint
import com.greenfodor.diceroller.geometry.triangleToTriangleTransform
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.DiceConstants.LIGHT_SOURCE
import com.greenfodor.diceroller.ui.dice.FaceLabelLayouts
import com.greenfodor.diceroller.ui.dice.PolyhedronFace
import com.greenfodor.diceroller.ui.dice.drawCenteredLabel
import com.greenfodor.diceroller.ui.dice.setAffine
import com.greenfodor.diceroller.ui.utils.shade

class D10Paints {
    val facePath = Path()
    val stroke = Stroke(width = DiceConstants.STROKE_WIDTH)
    val dstArray = FloatArray(6)
    val labelCoefficients = FloatArray(AFFINE_COEFFICIENT_COUNT)
    val labelMatrix = Matrix()
    val labelLayouts = FaceLabelLayouts()
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
    textMeasurer: TextMeasurer,
    color: Color,
    labelFor: (geometryValue: Int) -> String = { it.toString() }
) {
    calculateGeometry(size, centerX, centerY, rotationX, rotationY, rotationZ, paints)
    val visibleFaces = getVisibleAndSortedFaces(color, paints.rotatedVertices, labelFor)
    visibleFaces.forEach { (face, normal, _) ->
        renderD10Face(face, normal, paints.projectedVertices, paints, textMeasurer)
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
    paints: D10Paints,
    textMeasurer: TextMeasurer
) {
    val intensity = normal.dot(LIGHT_SOURCE).coerceIn(
        DiceConstants.MIN_SHADING_INTENSITY,
        DiceConstants.MAX_SHADING_INTENSITY
    )
    val shadedColor = face.baseColor.shade(intensity)
    val verts = face.vertexIndices.map { projectedVertices[it] }

    paints.facePath.rewind()
    paints.facePath.moveTo(verts[0].x, verts[0].y)
    for (i in 1 until verts.size) paints.facePath.lineTo(verts[i].x, verts[i].y)
    paints.facePath.close()

    drawPath(path = paints.facePath, color = shadedColor)
    drawFaceLabel(face.label, face.vertexIndices, projectedVertices, paints, textMeasurer)
    drawPath(
        path = paints.facePath,
        color = Color.White.copy(alpha = DiceConstants.D20_STROKE_ALPHA),
        style = paints.stroke
    )
}

private fun DrawScope.drawFaceLabel(
    label: String,
    vIndices: List<Int>,
    projectedVertices: List<Point2D>,
    paints: D10Paints,
    textMeasurer: TextMeasurer
) {
    // Both upper and lower faces use their apex (vIndices[0]) as the top reference,
    // with the two wing vertices (vIndices[1]=right, vIndices[3]=left) completing the triangle.
    paints.dstArray[0] = projectedVertices[vIndices[0]].x
    paints.dstArray[1] = projectedVertices[vIndices[0]].y
    paints.dstArray[2] = projectedVertices[vIndices[1]].x
    paints.dstArray[3] = projectedVertices[vIndices[1]].y
    paints.dstArray[4] = projectedVertices[vIndices[3]].x
    paints.dstArray[5] = projectedVertices[vIndices[3]].y

    if (!triangleToTriangleTransform(DiceConstants.D20_SRC_TRIANGLE, paints.dstArray, paints.labelCoefficients)) return
    paints.labelMatrix.setAffine(paints.labelCoefficients)

    val textColor = Color.White.copy(alpha = DiceConstants.D20_FACE_ALPHA)
    val labels = paints.labelLayouts
    val layout = labels.layout(textMeasurer, label, DiceConstants.D20_TEXT_SIZE_UV, textColor)

    withTransform({ transform(paints.labelMatrix) }) {
        val baseline = -(labels.descent + labels.ascent) / 2 + DiceConstants.D10_TEXT_BASELINE_ADJUSTMENT

        drawCenteredLabel(layout, baseline)

        if (label in DiceConstants.D20_AMBIGUOUS_LABELS) {
            drawRect(
                color = textColor,
                topLeft = Offset(
                    -DiceConstants.D10_UNDERLINE_WIDTH_UV / 2f,
                    baseline + DiceConstants.D10_UNDERLINE_TOP_OFFSET_UV
                ),
                size = Size(DiceConstants.D10_UNDERLINE_WIDTH_UV, DiceConstants.D10_UNDERLINE_HEIGHT_UV)
            )
        }
    }
}
