package com.greenfodor.diceroller.ui.dice.d20

import android.graphics.Matrix
import android.graphics.Paint as NativePaint
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
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

class D20Paints {
    val face = Paint()
    val stroke = Paint()
    val textPaint = NativePaint().apply {
        textAlign = NativePaint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }
    val pathEffect = PathEffect.cornerPathEffect(DiceConstants.CORNER_RADIUS / 2f)
}

fun DrawScope.drawD20(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    facePath: Path,
    paints: D20Paints,
    color: Color
) {
    val scaleFactor = size / 2f
    
    // 1. Geometry
    val rotatedVertices = ArrayList<Point3D>(12)
    val projectedVertices = ArrayList<Point2D>(12)
    
    IcosahedronGeometry.vertices.forEach { baseV ->
        val v = baseV * scaleFactor
        val rotated = v.rotatePoint(rotationX, rotationY, rotationZ)
        rotatedVertices.add(rotated)
        projectedVertices.add(rotated.projectPoint(centerX, centerY))
    }

    // 2. Culling and Sorting
    val faces = IcosahedronGeometry.faceIndices.mapIndexed { index, indices ->
        PolyhedronFace(indices, color, (index + 1).toString())
    }

    val visibleFaces = faces.mapNotNull { face ->
        val vIndices = face.vertexIndices
        val v0 = rotatedVertices[vIndices[0]]
        val v1 = rotatedVertices[vIndices[1]]
        val v2 = rotatedVertices[vIndices[2]]

        val normalZ = calculateNormalZ(v0, v1, v2)
        
        if (normalZ > 0) {
            val normal = (v1 - v0).cross(v2 - v0).normalize()
            val avgDepth = vIndices.sumOf { rotatedVertices[it].z.toDouble() }
            Triple(face, normal, avgDepth)
        } else null
    }.sortedBy { it.third }

    // 3. Rendering
    visibleFaces.forEach { (face, normal, _) ->
        renderD20Face(
            face = face,
            normal = normal,
            projectedVertices = projectedVertices,
            facePath = facePath,
            paints = paints
        )
    }
}

private fun DrawScope.renderD20Face(
    face: PolyhedronFace,
    normal: Point3D,
    projectedVertices: List<Point2D>,
    facePath: Path,
    paints: D20Paints
) {
    val intensity = normal.dot(LIGHT_SOURCE).coerceIn(
        DiceConstants.MIN_SHADING_INTENSITY,
        DiceConstants.MAX_SHADING_INTENSITY
    )
    val shadedColor = face.baseColor.shade(intensity)

    facePath.reset()
    val vIndices = face.vertexIndices
    facePath.moveTo(projectedVertices[vIndices[0]].x, projectedVertices[vIndices[0]].y)
    for (i in 1 until vIndices.size) {
        facePath.lineTo(projectedVertices[vIndices[i]].x, projectedVertices[vIndices[i]].y)
    }
    facePath.close()

    drawIntoCanvas { canvas ->
        paints.face.apply {
            color = shadedColor
            pathEffect = paints.pathEffect
            style = PaintingStyle.Fill
        }
        canvas.drawOutline(Outline.Generic(facePath), paints.face)
        
        // Draw label (number) "sitting flat" on the face using a transformation matrix
        val vIndices = face.vertexIndices
        canvas.nativeCanvas.withSave {
            val matrix = Matrix()
            // Source equilateral triangle points (UV space)
            // Centered at (0,0), radius 100
            val src = floatArrayOf(
                0f, -100f,
                86.6f, 50f,
                -86.6f, 50f
            )
            // Destination points from the 3D projected vertices
            val dst = floatArrayOf(
                projectedVertices[vIndices[0]].x, projectedVertices[vIndices[0]].y,
                projectedVertices[vIndices[1]].x, projectedVertices[vIndices[1]].y,
                projectedVertices[vIndices[2]].x, projectedVertices[vIndices[2]].y
            )
            matrix.setPolyToPoly(src, 0, dst, 0, 3)
            concat(matrix)

            paints.textPaint.apply {
                color = Color.White.copy(alpha = 0.9f).toArgb()
                textSize = 65f // Fixed size in UV space; matrix handles scaling and perspective
            }
            
            drawText(
                face.label,
                0f,
                -(paints.textPaint.descent() + paints.textPaint.ascent()) / 2 + 10f, // Center with small baseline adjustment
                paints.textPaint
            )
        }

        paints.stroke.apply {
            color = Color.White.copy(alpha = 0.3f)
            style = PaintingStyle.Stroke
            strokeWidth = DiceConstants.STROKE_WIDTH
            pathEffect = paints.pathEffect
        }
        canvas.drawOutline(Outline.Generic(facePath), paints.stroke)
    }
}

private fun Color.shade(intensity: Float) = Color(
    red = red * intensity,
    green = green * intensity,
    blue = blue * intensity,
    alpha = alpha
)
