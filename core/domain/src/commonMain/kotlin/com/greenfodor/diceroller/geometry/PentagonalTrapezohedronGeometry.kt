package com.greenfodor.diceroller.geometry

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Defines the geometry of a pentagonal trapezohedron (D10).
 *
 * A pentagonal trapezohedron has 12 vertices and 10 kite-shaped quadrilateral faces.
 * It is the dual of the pentagonal antiprism.
 *
 * Vertices are placed on a sphere of circumradius √3 to match the apparent size of the
 * other dice in this app. The 12 vertices are:
 *   - 1 top apex at z = +√3
 *   - 5 upper ring vertices near the equator (z ≈ +0.183), angles 0°, 72°, 144°, 216°, 288°
 *   - 5 lower ring vertices near the equator (z ≈ −0.183), angles 36°, 108°, 180°, 252°, 324°
 *   - 1 bottom apex at z = −√3
 *
 * The ring height ratio h/R = 0.10559 is the unique value that keeps all four vertices of
 * each kite face coplanar, producing a geometrically correct pentagonal trapezohedron.
 *
 * Odd values (1,3,5,7,9) are assigned to the upper faces; even values (10,8,6,4,2) to the
 * lower faces so that opposite faces sum to 11, matching the standard physical D10 convention.
 */
object PentagonalTrapezohedronGeometry {
    private val CIRCUMRADIUS = sqrt(3f)
    private val h = CIRCUMRADIUS * 0.10559f // ring height: only ratio giving planar kite faces
    private val ringRadius = sqrt(CIRCUMRADIUS * CIRCUMRADIUS - h * h) // ring vertices on the circumsphere

    private const val STEP = (2.0 * PI / 5.0).toFloat()
    private const val OFFSET = (PI / 5.0).toFloat()

    val vertices = listOf(
        Point3D(0f, 0f, CIRCUMRADIUS), // 0: top apex
        Point3D(ringRadius * cos(0 * STEP), ringRadius * sin(0 * STEP), h), // 1: U0 (0°)
        Point3D(ringRadius * cos(1 * STEP), ringRadius * sin(1 * STEP), h), // 2: U1 (72°)
        Point3D(ringRadius * cos(2 * STEP), ringRadius * sin(2 * STEP), h), // 3: U2 (144°)
        Point3D(ringRadius * cos(3 * STEP), ringRadius * sin(3 * STEP), h), // 4: U3 (216°)
        Point3D(ringRadius * cos(4 * STEP), ringRadius * sin(4 * STEP), h), // 5: U4 (288°)
        Point3D(ringRadius * cos(OFFSET), ringRadius * sin(OFFSET), -h), // 6: L0 (36°)
        Point3D(ringRadius * cos(OFFSET + STEP), ringRadius * sin(OFFSET + STEP), -h), // 7: L1 (108°)
        Point3D(ringRadius * cos(OFFSET + 2 * STEP), ringRadius * sin(OFFSET + 2 * STEP), -h), // 8: L2 (180°)
        Point3D(ringRadius * cos(OFFSET + 3 * STEP), ringRadius * sin(OFFSET + 3 * STEP), -h), // 9: L3 (252°)
        Point3D(ringRadius * cos(OFFSET + 4 * STEP), ringRadius * sin(OFFSET + 4 * STEP), -h), // 10: L4 (324°)
        Point3D(0f, 0f, -CIRCUMRADIUS) // 11: bottom apex
    )

    /**
     * Ten kite-shaped faces. Each face has 4 vertices in CCW winding when viewed from outside.
     *
     * Upper faces: [top_apex, U_i, L_i, U_{(i+1)%5}]
     *   The top apex is vertex 0 in the face list (used as "up" reference in getFaceRotation).
     *
     * Lower faces: [bottom_apex, L_{(i+1)%5}, U_{(i+1)%5}, L_i]
     *   The U vertex (index 2 in the face list) is used as "up" reference in getFaceRotation.
     */
    val faces = listOf(
        GeometryFace(value = 1, vertexIndices = listOf(0, 1, 6, 2)), // apex, U0, L0, U1
        GeometryFace(value = 3, vertexIndices = listOf(0, 2, 7, 3)), // apex, U1, L1, U2
        GeometryFace(value = 5, vertexIndices = listOf(0, 3, 8, 4)), // apex, U2, L2, U3
        GeometryFace(value = 7, vertexIndices = listOf(0, 4, 9, 5)), // apex, U3, L3, U4
        GeometryFace(value = 9, vertexIndices = listOf(0, 5, 10, 1)), // apex, U4, L4, U0
        GeometryFace(value = 10, vertexIndices = listOf(11, 7, 2, 6)), // bottom, L1, U1, L0
        GeometryFace(value = 8, vertexIndices = listOf(11, 8, 3, 7)), // bottom, L2, U2, L1
        GeometryFace(value = 6, vertexIndices = listOf(11, 9, 4, 8)), // bottom, L3, U3, L2
        GeometryFace(value = 4, vertexIndices = listOf(11, 10, 5, 9)), // bottom, L4, U4, L3
        GeometryFace(value = 2, vertexIndices = listOf(11, 6, 1, 10)) // bottom, L0, U0, L4
    )

    /**
     * Returns the Euler rotation (X, Y, Z degrees) to bring [face] front-facing and upright.
     *
     * Both upper and lower faces use their apex vertex (index 0 in the face list) as the
     * "up" reference, so the pointy tip is always at the top — matching a real D10.
     */
    fun getFaceRotation(face: GeometryFace): Triple<Float, Float, Float> {
        val v0 = vertices[face.vertexIndices[0]]
        val v1 = vertices[face.vertexIndices[1]]
        val v2 = vertices[face.vertexIndices[2]]

        val normal = (v1 - v0).cross(v2 - v0).normalize()

        val rx = atan2(normal.y, normal.z)
        val rxDeg = rx * 180f / PI.toFloat()

        val ry = atan2(-normal.x, sqrt(normal.y * normal.y + normal.z * normal.z))
        val ryDeg = ry * 180f / PI.toFloat()

        val faceVertices = face.vertexIndices.map { vertices[it] }
        val center = faceVertices.reduce { acc, v -> acc + v } * (1f / faceVertices.size)

        // Both upper and lower faces use index 0 (their respective apex) as the "up" reference
        // so the pointy tip is always at the top, matching the physical D10 convention.
        val topRefIndex = 0
        val topRef = vertices[face.vertexIndices[topRefIndex]]

        val topRefRotated = topRef.rotatePoint(rxDeg, ryDeg, 0f)
        val centerRotated = center.rotatePoint(rxDeg, ryDeg, 0f)

        val dir = topRefRotated - centerRotated
        val currentAngle = atan2(dir.y.toDouble(), dir.x.toDouble()).toFloat()

        val rz = -(PI.toFloat() / 2f + currentAngle)
        val rzDeg = rz * 180f / PI.toFloat()

        return Triple(rxDeg, ryDeg, rzDeg)
    }
}
