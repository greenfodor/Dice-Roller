package com.greenfodor.diceroller.geometry

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Defines the geometry of a regular icosahedron (20-sided die).
 */
object IcosahedronGeometry {
    private val PHI = (1f + sqrt(5f)) / 2f

    /**
     * The 12 vertices of a regular icosahedron.
     */
    val vertices = listOf(
        Point3D(-1f,  PHI, 0f),  // 0
        Point3D( 1f,  PHI, 0f),  // 1
        Point3D(-1f, -PHI, 0f),  // 2
        Point3D( 1f, -PHI, 0f),  // 3

        Point3D(0f, -1f,  PHI),  // 4
        Point3D(0f,  1f,  PHI),  // 5
        Point3D(0f, -1f, -PHI),  // 6
        Point3D(0f,  1f, -PHI),  // 7

        Point3D( PHI, 0f, -1f),  // 8
        Point3D( PHI, 0f,  1f),  // 9
        Point3D(-PHI, 0f, -1f),  // 10
        Point3D(-PHI, 0f,  1f)   // 11
    )

    /**
     * The 20 triangular faces of the icosahedron, defined by vertex indices.
     * Order is counter-clockwise when viewed from the outside.
     */
    val faceIndices = listOf(
        listOf(0, 11, 5),  // 0
        listOf(0, 5, 1),   // 1
        listOf(0, 1, 7),   // 2
        listOf(0, 7, 10),  // 3
        listOf(0, 10, 11), // 4

        listOf(1, 5, 9),   // 5
        listOf(5, 11, 4),  // 6
        listOf(11, 10, 2), // 7
        listOf(10, 7, 6),  // 8
        listOf(7, 1, 8),   // 9

        listOf(3, 9, 4),   // 10
        listOf(3, 4, 2),   // 11
        listOf(3, 2, 6),   // 12
        listOf(3, 6, 8),   // 13
        listOf(3, 8, 9),   // 14

        listOf(4, 9, 5),   // 15
        listOf(2, 4, 11),  // 16
        listOf(6, 2, 10),  // 17
        listOf(8, 6, 7),   // 18
        listOf(9, 8, 1)    // 19
    )

    /**
     * Calculates the rotation (X, Y) required to make a specific face front-facing.
     */
    fun getFaceRotation(faceIndex: Int): Pair<Float, Float> {
        val indices = faceIndices[faceIndex]
        val v0 = vertices[indices[0]]
        val v1 = vertices[indices[1]]
        val v2 = vertices[indices[2]]

        // Normal points outward
        val normal = (v1 - v0).cross(v2 - v0).normalize()

        // To make the face point to +Z, we need to rotate the normal to (0, 0, 1)
        // Rotation order in our rotatePoint is X then Y.
        // Let's find angles that rotate (0, 0, 1) to 'normal', then we use the negative for target.
        
        // Vertical angle (X-axis rotation)
        val rotX = -atan2(normal.y, sqrt(normal.x * normal.x + normal.z * normal.z))
        
        // Horizontal angle (Y-axis rotation)
        // After rotating by rotX, the normal is in the XZ plane.
        val rotY = atan2(normal.x, normal.z)

        return Pair(
            (rotX * 180f / PI.toFloat()),
            (rotY * 180f / PI.toFloat())
        )
    }
}
