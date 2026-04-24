package com.greenfodor.diceroller.geometry

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Defines the geometry of a regular icosahedron (20-sided die).
 *
 * An icosahedron is composed of 12 vertices and 20 equilateral triangular faces.
 * The vertices are defined based on the golden ratio (PHI) to ensure perfect regularity.
 */
object IcosahedronGeometry {
    private val PHI = (1f + sqrt(5f)) / 2f

    /**
     * The 12 vertices of a regular icosahedron centered at the origin.
     */
    val vertices =
        listOf(
            Point3D(-1f, PHI, 0f), // 0
            Point3D(1f, PHI, 0f), // 1
            Point3D(-1f, -PHI, 0f), // 2
            Point3D(1f, -PHI, 0f), // 3
            Point3D(0f, -1f, PHI), // 4
            Point3D(0f, 1f, PHI), // 5
            Point3D(0f, -1f, -PHI), // 6
            Point3D(0f, 1f, -PHI), // 7
            Point3D(PHI, 0f, -1f), // 8
            Point3D(PHI, 0f, 1f), // 9
            Point3D(-PHI, 0f, -1f), // 10
            Point3D(-PHI, 0f, 1f), // 11
        )

    /**
     * The 20 triangular faces of the icosahedron, defined by vertex indices.
     * Order is counter-clockwise when viewed from the outside.
     * The FIRST vertex in each list is considered the "top" of the face.
     */
    val faceIndices =
        listOf(
            listOf(5, 0, 11), // 0
            listOf(1, 0, 5), // 1
            listOf(7, 0, 1), // 2
            listOf(10, 0, 7), // 3
            listOf(11, 0, 10), // 4
            listOf(9, 1, 5), // 5
            listOf(4, 5, 11), // 6
            listOf(2, 11, 10), // 7
            listOf(6, 10, 7), // 8
            listOf(8, 7, 1), // 9
            listOf(3, 9, 4), // 10
            listOf(3, 4, 2), // 11
            listOf(3, 2, 6), // 12
            listOf(3, 6, 8), // 13
            listOf(3, 8, 9), // 14
            listOf(5, 4, 9), // 15
            listOf(11, 2, 4), // 16
            listOf(10, 6, 2), // 17
            listOf(7, 8, 6), // 18
            listOf(1, 9, 8), // 19
        )

    /**
     * Calculates the rotation (X, Y, Z) required to make a specific face front-facing and upright.
     * Upright means the FIRST vertex of the face is directly above the face's center.
     */
    fun getFaceRotation(faceIndex: Int): Triple<Float, Float, Float> {
        val indices = faceIndices[faceIndex]
        val v0 = vertices[indices[0]]
        val v1 = vertices[indices[1]]
        val v2 = vertices[indices[2]]

        // Outward normal
        val normal = (v1 - v0).cross(v2 - v0).normalize()

        // 1. Calculate X rotation to make normal's Y = 0
        // Standard rotation: y' = y cos - z sin = 0 => tan rx = y/z
        val rx = atan2(normal.y, normal.z)
        val rxDeg = rx * 180f / PI.toFloat()

        // 2. Calculate Y rotation to make normal's X = 0
        // After Rx, nz' = sqrt(ny^2 + nz^2). x' = nx.
        // Rotation: x'' = x' cos + z' sin = 0 => tan ry = -x'/z'
        val ry = atan2(-normal.x, sqrt(normal.y * normal.y + normal.z * normal.z))
        val ryDeg = ry * 180f / PI.toFloat()

        // 3. Calculate Z rotation to make the first vertex point "up"
        val center = (v0 + v1 + v2) * (1f / 3f)
        val v0Rotated = v0.rotatePoint(rxDeg, ryDeg, 0f)
        val centerRotated = center.rotatePoint(rxDeg, ryDeg, 0f)

        // Vector from center to first vertex in the XY plane
        val dir = v0Rotated - centerRotated
        val currentAngle = atan2(dir.y, dir.x)

        // Target angle is -PI/2 (straight up in screen space)
        val rz = -(PI.toFloat() / 2f + currentAngle)
        val rzDeg = rz * 180f / PI.toFloat()

        return Triple(rxDeg, ryDeg, rzDeg)
    }
}
