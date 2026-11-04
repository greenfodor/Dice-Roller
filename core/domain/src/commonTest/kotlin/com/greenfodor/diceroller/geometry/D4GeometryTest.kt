package com.greenfodor.diceroller.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class D4GeometryTest {
    @Test
    fun `tetrahedron has 4 vertices and 4 faces`() {
        assertEquals(4, TetrahedronGeometry.vertices.size)
        assertEquals(4, TetrahedronGeometry.faces.size)
    }

    @Test
    fun `face values cover 1 through 4 without duplicates`() {
        val values = TetrahedronGeometry.faces.map { it.value }.sorted()
        assertEquals(listOf(1, 2, 3, 4), values, "Should have 4 faces")
    }

    @Test
    fun `all D4 faces have outward pointing normals`() {
        TetrahedronGeometry.faces.forEach { face ->
            val v0 = TetrahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = TetrahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = TetrahedronGeometry.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0)

            val center = (v0 + v1 + v2) * (1f / 3f)
            assertTrue(
                normal.dot(center) > 0,
                "Face ${face.value} normal should point outward (dot product: ${normal.dot(center)})"
            )
        }
    }

    @Test
    fun `getFaceRotation brings target face normal to camera direction`() {
        TetrahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = TetrahedronGeometry.getFaceRotation(face)

            val v0 = TetrahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = TetrahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = TetrahedronGeometry.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0).normalize()
            val rotatedNormal = normal.rotatePoint(rx, ry, rz)

            assertEquals(0f, rotatedNormal.x, 0.01f, "Face ${face.value}: Normal X should be 0")
            assertEquals(0f, rotatedNormal.y, 0.01f, "Face ${face.value}: Normal Y should be 0")
            assertTrue(rotatedNormal.z > 0.99f, "Face ${face.value}: Normal Z should be positive")
        }
    }

    @Test
    fun `getFaceRotation orients first vertex to top of screen`() {
        TetrahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = TetrahedronGeometry.getFaceRotation(face)

            val v0 = TetrahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = TetrahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = TetrahedronGeometry.vertices[face.vertexIndices[2]]
            val vCenter = (v0 + v1 + v2) * (1f / 3f)

            val v0Rotated = v0.rotatePoint(rx, ry, rz)
            val centerRotated = vCenter.rotatePoint(rx, ry, rz)

            assertEquals(
                centerRotated.x,
                v0Rotated.x,
                0.01f,
                "Face ${face.value}: Top vertex should be horizontally centered"
            )
            assertTrue(
                v0Rotated.y < centerRotated.y,
                "Face ${face.value}: Top vertex should be above center (smaller Y)"
            )
        }
    }
}
