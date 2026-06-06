package com.greenfodor.diceroller.geometry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class D8GeometryTest {
    @Test
    fun `octahedron has 6 vertices and 8 faces`() {
        assertEquals(6, OctahedronGeometry.vertices.size)
        assertEquals(8, OctahedronGeometry.faces.size)
    }

    @Test
    fun `all vertices lie on the same circumsphere`() {
        val radii = OctahedronGeometry.vertices.map { v ->
            sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
        }
        val expected = radii.first()
        radii.forEach { r ->
            assertEquals("All vertices should have equal circumradius", expected, r, 0.001f)
        }
    }

    @Test
    fun `circumradius matches D4 and D6 for consistent apparent size`() {
        val expectedRadius = sqrt(3f)
        OctahedronGeometry.vertices.forEach { v ->
            val radius = sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
            assertEquals("Circumradius should be √3", expectedRadius, radius, 0.001f)
        }
    }

    @Test
    fun `face values cover 1 through 8 without duplicates`() {
        val values = OctahedronGeometry.faces.map { it.value }.sorted()
        assertEquals("Should have 8 faces", listOf(1, 2, 3, 4, 5, 6, 7, 8), values)
    }

    @Test
    fun `opposite faces sum to 9`() {
        val faceNormals = OctahedronGeometry.faces.map { face ->
            val v0 = OctahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = OctahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = OctahedronGeometry.vertices[face.vertexIndices[2]]
            val normal = (v1 - v0).cross(v2 - v0).normalize()
            face.value to normal
        }

        faceNormals.forEach { (value, normal) ->
            val opposite = faceNormals.find { (_, otherNormal) ->
                normal.dot(otherNormal) < -0.99f
            }
            assertTrue("Face $value should have an antipodal face", opposite != null)
            assertEquals(
                "Face $value and its opposite should sum to 9",
                9,
                value + opposite!!.first
            )
        }
    }

    @Test
    fun `all D8 faces have outward pointing normals`() {
        OctahedronGeometry.faces.forEach { face ->
            val v0 = OctahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = OctahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = OctahedronGeometry.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0)
            val center = (v0 + v1 + v2) * (1f / 3f)

            assertTrue(
                "Face ${face.value} normal should point outward (dot product: ${normal.dot(center)})",
                normal.dot(center) > 0
            )
        }
    }

    @Test
    fun `getFaceRotation brings target face normal to camera direction`() {
        OctahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = OctahedronGeometry.getFaceRotation(face)

            val v0 = OctahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = OctahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = OctahedronGeometry.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0).normalize()
            val rotatedNormal = normal.rotatePoint(rx, ry, rz)

            assertEquals("Face ${face.value}: Normal X should be 0", 0f, rotatedNormal.x, 0.01f)
            assertEquals("Face ${face.value}: Normal Y should be 0", 0f, rotatedNormal.y, 0.01f)
            assertTrue("Face ${face.value}: Normal Z should be positive", rotatedNormal.z > 0.99f)
        }
    }

    @Test
    fun `getFaceRotation orients first vertex to top of screen`() {
        OctahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = OctahedronGeometry.getFaceRotation(face)

            val v0 = OctahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = OctahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = OctahedronGeometry.vertices[face.vertexIndices[2]]
            val vCenter = (v0 + v1 + v2) * (1f / 3f)

            val v0Rotated = v0.rotatePoint(rx, ry, rz)
            val centerRotated = vCenter.rotatePoint(rx, ry, rz)

            assertEquals(
                "Face ${face.value}: Top vertex should be horizontally centered",
                centerRotated.x,
                v0Rotated.x,
                0.01f
            )
            assertTrue(
                "Face ${face.value}: Top vertex should be above center (smaller Y)",
                v0Rotated.y < centerRotated.y
            )
        }
    }
}
