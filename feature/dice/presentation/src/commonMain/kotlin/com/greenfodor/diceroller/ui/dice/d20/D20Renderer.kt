package com.greenfodor.diceroller.ui.dice.d20

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
import com.greenfodor.diceroller.geometry.IcosahedronGeometry
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

/**
 * Holds reusable paths, matrices, styles, text layouts and temporary buffers used for D20 face
 * rendering.
 */
class D20Paints {
    /** Reusable path for face polygon geometry. */
    val facePath = Path()

    /** Stroke style for face borders. */
    val stroke = Stroke(width = DiceConstants.STROKE_WIDTH)

    /** Temporary buffer for the label transform's destination triangle. */
    val dstArray = FloatArray(6)

    /** Affine coefficients of the label transform. */
    val labelCoefficients = FloatArray(AFFINE_COEFFICIENT_COUNT)

    /** Reusable matrix projecting a label onto its face. */
    val labelMatrix = Matrix()

    /** Cached text layouts of the face labels. */
    val labelLayouts = FaceLabelLayouts()

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
 * @param textMeasurer Measures the face labels.
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
    textMeasurer: TextMeasurer,
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
            paints = paints,
            textMeasurer = textMeasurer
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
    paints: D20Paints,
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
    paints: D20Paints,
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
        val textY = -(labels.descent + labels.ascent) / 2 + DiceConstants.D20_TEXT_BASELINE_ADJUSTMENT
        drawCenteredLabel(layout, textY)

        if (label in DiceConstants.D20_AMBIGUOUS_LABELS) {
            val underlineY = textY + labels.descent + DiceConstants.D20_UNDERLINE_TOP_OFFSET_UV
            drawRect(
                color = textColor,
                topLeft = Offset(-DiceConstants.D20_UNDERLINE_WIDTH_UV / 2, underlineY),
                size = Size(DiceConstants.D20_UNDERLINE_WIDTH_UV, DiceConstants.D20_UNDERLINE_HEIGHT_UV)
            )
        }
    }
}
