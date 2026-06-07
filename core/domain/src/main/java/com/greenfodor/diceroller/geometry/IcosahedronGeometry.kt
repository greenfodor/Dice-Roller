package com.greenfodor.diceroller.geometry

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
    val vertices = listOf(
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
        Point3D(-PHI, 0f, 1f) // 11
    )

    /**
     * The 20 triangular faces of the icosahedron, each mapped to a die value.
     * Vertex order is counter-clockwise when viewed from the outside.
     * The FIRST vertex in each list is considered the "top" of the face.
     */
    val faces = listOf(
        GeometryFace(value = 1, vertexIndices = listOf(5, 0, 11)),
        GeometryFace(value = 2, vertexIndices = listOf(1, 0, 5)),
        GeometryFace(value = 3, vertexIndices = listOf(7, 0, 1)),
        GeometryFace(value = 4, vertexIndices = listOf(10, 0, 7)),
        GeometryFace(value = 5, vertexIndices = listOf(11, 0, 10)),
        GeometryFace(value = 6, vertexIndices = listOf(9, 1, 5)),
        GeometryFace(value = 7, vertexIndices = listOf(4, 5, 11)),
        GeometryFace(value = 8, vertexIndices = listOf(2, 11, 10)),
        GeometryFace(value = 9, vertexIndices = listOf(6, 10, 7)),
        GeometryFace(value = 10, vertexIndices = listOf(8, 7, 1)),
        GeometryFace(value = 11, vertexIndices = listOf(3, 9, 4)),
        GeometryFace(value = 12, vertexIndices = listOf(3, 4, 2)),
        GeometryFace(value = 13, vertexIndices = listOf(3, 2, 6)),
        GeometryFace(value = 14, vertexIndices = listOf(3, 6, 8)),
        GeometryFace(value = 15, vertexIndices = listOf(3, 8, 9)),
        GeometryFace(value = 16, vertexIndices = listOf(5, 4, 9)),
        GeometryFace(value = 17, vertexIndices = listOf(11, 2, 4)),
        GeometryFace(value = 18, vertexIndices = listOf(10, 6, 2)),
        GeometryFace(value = 19, vertexIndices = listOf(7, 8, 6)),
        GeometryFace(value = 20, vertexIndices = listOf(1, 9, 8))
    )

    fun getFaceRotation(face: GeometryFace): Triple<Float, Float, Float> =
        calculateFaceRotation(vertices, face)
}
