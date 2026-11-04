package com.greenfodor.diceroller.ui.dice.d4

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import com.greenfodor.diceroller.geometry.AFFINE_COEFFICIENT_COUNT
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.TetrahedronGeometry
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

/**
 * Holds reusable paths, matrices, styles, text layouts and temporary buffers used for D4 face
 * rendering.
 */
class D4Paints {
    val facePath = Path()
    val stroke = Stroke(width = DiceConstants.STROKE_WIDTH)
    val dstArray = FloatArray(6)
    val labelCoefficients = FloatArray(AFFINE_COEFFICIENT_COUNT)
    val labelMatrix = Matrix()
    val labelLayouts = FaceLabelLayouts()
    val rotatedVertices = ArrayList<Point3D>(TetrahedronGeometry.vertices.size).apply {
        repeat(TetrahedronGeometry.vertices.size) { add(Point3D(0f, 0f, 0f)) }
    }
    val projectedVertices = ArrayList<Point2D>(TetrahedronGeometry.vertices.size).apply {
        repeat(TetrahedronGeometry.vertices.size) { add(Point2D(0f, 0f)) }
    }
}

fun DrawScope.drawD4(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D4Paints,
    textMeasurer: TextMeasurer,
    color: Color
) {
    calculateGeometry(size, centerX, centerY, rotationX, rotationY, rotationZ, paints)
    val visibleFaces = getVisibleAndSortedFaces(color, paints.rotatedVertices)
    visibleFaces.forEach { (face, normal, _) ->
        renderD4Face(face, normal, paints.projectedVertices, paints, textMeasurer)
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
    paints: D4Paints,
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
    paints: D4Paints,
    textMeasurer: TextMeasurer
) {
    paints.dstArray[0] = projectedVertices[vIndices[0]].x
    paints.dstArray[1] = projectedVertices[vIndices[0]].y
    paints.dstArray[2] = projectedVertices[vIndices[1]].x
    paints.dstArray[3] = projectedVertices[vIndices[1]].y
    paints.dstArray[4] = projectedVertices[vIndices[2]].x
    paints.dstArray[5] = projectedVertices[vIndices[2]].y

    if (!triangleToTriangleTransform(DiceConstants.D20_SRC_TRIANGLE, paints.dstArray, paints.labelCoefficients)) return
    paints.labelMatrix.setAffine(paints.labelCoefficients)

    val textColor = Color.White.copy(alpha = DiceConstants.D20_FACE_ALPHA)
    val labels = paints.labelLayouts
    val layout = labels.layout(textMeasurer, label, DiceConstants.D20_TEXT_SIZE_UV, textColor)

    withTransform({ transform(paints.labelMatrix) }) {
        drawCenteredLabel(
            layout,
            -(labels.descent + labels.ascent) / 2 + DiceConstants.D20_TEXT_BASELINE_ADJUSTMENT
        )
    }
}
