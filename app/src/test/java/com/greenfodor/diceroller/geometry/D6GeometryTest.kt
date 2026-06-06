package com.greenfodor.diceroller.geometry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class D6GeometryTest {
    @Test
    fun `hexahedron has 8 vertices and 6 faces`() {
        assertEquals(8, HexahedronGeometry.vertices.size)
        assertEquals(6, HexahedronGeometry.faces.size)
    }

    @Test
    fun `all vertices lie on the same circumsphere`() {
        val radii = HexahedronGeometry.vertices.map { v ->
            sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
        }
        val expected = radii.first()
        radii.forEach { r ->
            assertEquals("All vertices should have equal circumradius", expected, r, 0.001f)
        }
    }

    @Test
    fun `circumradius matches D4 and D8 for consistent apparent size`() {
        val expectedRadius = sqrt(3f)
        HexahedronGeometry.vertices.forEach { v ->
            val radius = sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
            assertEquals("Circumradius should be √3", expectedRadius, radius, 0.001f)
        }
    }

    @Test
    fun `face values cover 1 through 6 without duplicates`() {
        val values = HexahedronGeometry.faces.map { it.value }.sorted()
        assertEquals("Should have 6 faces", listOf(1, 2, 3, 4, 5, 6), values)
    }

    @Test
    fun `opposite faces sum to 7`() {
        val faceNormals = HexahedronGeometry.faces.map { face ->
            val v0 = HexahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = HexahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = HexahedronGeometry.vertices[face.vertexIndices[2]]
            val normal = (v1 - v0).cross(v2 - v0).normalize()
            face.value to normal
        }

        faceNormals.forEach { (value, normal) ->
            val opposite = faceNormals.find { (_, otherNormal) ->
                normal.dot(otherNormal) < -0.99f
            }
            assertTrue("Face $value should have an antipodal face", opposite != null)
            assertEquals(
                "Face $value and its opposite should sum to 7",
                7,
                value + opposite!!.first
            )
        }
    }

    @Test
    fun `all D6 faces have outward pointing normals`() {
        HexahedronGeometry.faces.forEach { face ->
            val v0 = HexahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = HexahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = HexahedronGeometry.vertices[face.vertexIndices[2]]
            val v3 = HexahedronGeometry.vertices[face.vertexIndices[3]]

            val normal = (v1 - v0).cross(v2 - v0)
            val center = (v0 + v1 + v2 + v3) * 0.25f

            assertTrue(
                "Face ${face.value} normal should point outward (dot product: ${normal.dot(center)})",
                normal.dot(center) > 0
            )
        }
    }

    @Test
    fun `getFaceRotation brings target face normal to camera direction`() {
        HexahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = HexahedronGeometry.getFaceRotation(face)

            val v0 = HexahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = HexahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = HexahedronGeometry.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0).normalize()
            val rotatedNormal = normal.rotatePoint(rx, ry, rz)

            assertEquals("Face ${face.value}: Normal X should be 0", 0f, rotatedNormal.x, 0.01f)
            assertEquals("Face ${face.value}: Normal Y should be 0", 0f, rotatedNormal.y, 0.01f)
            assertTrue("Face ${face.value}: Normal Z should be positive", rotatedNormal.z > 0.99f)
        }
    }

    @Test
    fun `getFaceRotation orients face to be axis-aligned (flat)`() {
        HexahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = HexahedronGeometry.getFaceRotation(face)

            val vIndices = face.vertexIndices
            val rotatedVertices = vIndices.map { HexahedronGeometry.vertices[it].rotatePoint(rx, ry, rz) }

            // In a flat/axis-aligned square, top/bottom edges are horizontal (equal Y)
            // and left/right edges are vertical (equal X).
            // Indices 2 and 3 are the 'top' edge in our winding.
            assertEquals(
                "Face ${face.value}: Top edge should be horizontal",
                rotatedVertices[2].y,
                rotatedVertices[3].y,
                0.01f
            )
        }
    }
}
