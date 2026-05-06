package com.greenfodor.diceroller.geometry

/**
 * Defines the geometry of a regular tetrahedron (D4).
 *
 * A tetrahedron is composed of 4 vertices and 4 equilateral triangular faces.
 * The vertices are the 4 alternating corners of a unit cube, which guarantees regularity.
 * Opposite edges are perpendicular and all edge lengths are equal (2√2).
 */
object TetrahedronGeometry {
    val vertices = listOf(
        Point3D(1f, 1f, 1f), // 0
        Point3D(1f, -1f, -1f), // 1
        Point3D(-1f, 1f, -1f), // 2
        Point3D(-1f, -1f, 1f) // 3
    )

    val faces = listOf(
        GeometryFace(value = 1, vertexIndices = listOf(0, 1, 2)),
        GeometryFace(value = 2, vertexIndices = listOf(0, 3, 1)),
        GeometryFace(value = 3, vertexIndices = listOf(0, 2, 3)),
        GeometryFace(value = 4, vertexIndices = listOf(1, 3, 2))
    )

    fun getFaceRotation(face: GeometryFace): Triple<Float, Float, Float> =
        calculateFaceRotation(vertices, face)
}
