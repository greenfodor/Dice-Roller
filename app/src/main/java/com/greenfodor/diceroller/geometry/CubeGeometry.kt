package com.greenfodor.diceroller.geometry

/**
 * Defines the geometry of a regular cube (D6).
 *
 * Vertices are indexed 0–7 and faces follow the standard dice layout where
 * opposite faces sum to 7 (1/6, 2/5, 3/4).
 */
object CubeGeometry {
    val vertices = listOf(
        Point3D(-1f, -1f, -1f), // 0
        Point3D(1f, -1f, -1f), // 1
        Point3D(1f, 1f, -1f), // 2
        Point3D(-1f, 1f, -1f), // 3
        Point3D(-1f, -1f, 1f), // 4
        Point3D(1f, -1f, 1f), // 5
        Point3D(1f, 1f, 1f), // 6
        Point3D(-1f, 1f, 1f) // 7
    )

    val faces = listOf(
        GeometryFace(value = 1, vertexIndices = listOf(4, 5, 6, 7)), // Front  (Z+)
        GeometryFace(value = 2, vertexIndices = listOf(0, 1, 5, 4)), // Bottom (Y-)
        GeometryFace(value = 3, vertexIndices = listOf(5, 1, 2, 6)), // Right  (X+)
        GeometryFace(value = 4, vertexIndices = listOf(0, 4, 7, 3)), // Left   (X-)
        GeometryFace(value = 5, vertexIndices = listOf(7, 6, 2, 3)), // Top    (Y+)
        GeometryFace(value = 6, vertexIndices = listOf(1, 0, 3, 2)) // Back   (Z-)
    )
}
