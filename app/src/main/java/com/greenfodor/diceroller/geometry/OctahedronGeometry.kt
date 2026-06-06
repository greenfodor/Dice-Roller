package com.greenfodor.diceroller.geometry

import kotlin.math.sqrt

/**
 * Defines the geometry of a regular octahedron (D8).
 *
 * An octahedron is composed of 6 vertices and 8 equilateral triangular faces.
 * Vertices are scaled to circumradius √3 so the die renders at the same apparent
 * size as the D4 and D6, whose vertices also sit at circumradius √3.
 * Opposite faces sum to 9, matching the standard physical D8 convention.
 */
object OctahedronGeometry {
    private val R = sqrt(3f)

    val vertices = listOf(
        Point3D(R, 0f, 0f), // 0: +X
        Point3D(-R, 0f, 0f), // 1: -X
        Point3D(0f, R, 0f), // 2: +Y
        Point3D(0f, -R, 0f), // 3: -Y
        Point3D(0f, 0f, R), // 4: +Z
        Point3D(0f, 0f, -R) // 5: -Z
    )

    /**
     * The 8 triangular faces of the octahedron, each mapped to a die value.
     * Vertex order is counter-clockwise when viewed from the outside.
     * The FIRST vertex in each list is considered the "top" of the face.
     * Opposite faces sum to 9 (1+8, 2+7, 3+6, 4+5).
     */
    val faces = listOf(
        GeometryFace(value = 1, vertexIndices = listOf(2, 4, 0)), // normal (+x+y+z)
        GeometryFace(value = 2, vertexIndices = listOf(2, 1, 4)), // normal (-x+y+z)
        GeometryFace(value = 3, vertexIndices = listOf(2, 5, 1)), // normal (-x+y-z)
        GeometryFace(value = 4, vertexIndices = listOf(2, 0, 5)), // normal (+x+y-z)
        GeometryFace(value = 5, vertexIndices = listOf(3, 4, 1)), // normal (-x-y+z)
        GeometryFace(value = 6, vertexIndices = listOf(3, 0, 4)), // normal (+x-y+z)
        GeometryFace(value = 7, vertexIndices = listOf(3, 5, 0)), // normal (+x-y-z)
        GeometryFace(value = 8, vertexIndices = listOf(3, 1, 5)) // normal (-x-y-z)
    )

    fun getFaceRotation(face: GeometryFace): Triple<Float, Float, Float> =
        calculateFaceRotation(vertices, face)
}
