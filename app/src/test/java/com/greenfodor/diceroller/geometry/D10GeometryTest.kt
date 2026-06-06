package com.greenfodor.diceroller.geometry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.sqrt

class D10GeometryTest {
    private val geo = PentagonalTrapezohedronGeometry

    @Test
    fun `pentagonal trapezohedron has 12 vertices and 10 faces`() {
        assertEquals(12, geo.vertices.size)
        assertEquals(10, geo.faces.size)
    }

    @Test
    fun `face values cover 1 through 10 without duplicates`() {
        val values = geo.faces.map { it.value }.sorted()
        assertEquals("Should have 10 faces", (1..10).toList(), values)
    }

    @Test
    fun `all D10 faces have outward pointing normals`() {
        geo.faces.forEach { face ->
            val v0 = geo.vertices[face.vertexIndices[0]]
            val v1 = geo.vertices[face.vertexIndices[1]]
            val v2 = geo.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0)

            val faceVerts = face.vertexIndices.map { geo.vertices[it] }
            val center = faceVerts.reduce { acc, v -> acc + v } * (1f / faceVerts.size)

            assertTrue(
                "Face ${face.value} normal should point outward (dot product: ${normal.dot(center)})",
                normal.dot(center) > 0
            )
        }
    }

    @Test
    fun `getFaceRotation brings target face normal to camera direction`() {
        geo.faces.forEach { face ->
            val (rx, ry, rz) = geo.getFaceRotation(face)

            val v0 = geo.vertices[face.vertexIndices[0]]
            val v1 = geo.vertices[face.vertexIndices[1]]
            val v2 = geo.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0).normalize()
            val rotatedNormal = normal.rotatePoint(rx, ry, rz)

            assertEquals("Face ${face.value}: Normal X should be 0", 0f, rotatedNormal.x, 0.01f)
            assertEquals("Face ${face.value}: Normal Y should be 0", 0f, rotatedNormal.y, 0.01f)
            assertTrue("Face ${face.value}: Normal Z should be positive", rotatedNormal.z > 0.99f)
        }
    }

    @Test
    fun `opposite faces sum to 11`() {
        val upperFaces = geo.faces.filter { it.vertexIndices[0] == 0 }
        val lowerFaces = geo.faces.filter { it.vertexIndices[0] == 11 }

        assertEquals("Should have 5 upper faces", 5, upperFaces.size)
        assertEquals("Should have 5 lower faces", 5, lowerFaces.size)

        val upperValues = upperFaces.map { it.value }.toSet()
        val lowerValues = lowerFaces.map { it.value }.toSet()

        upperValues.forEach { v ->
            assertTrue(
                "Opposite of $v should sum to 11 (${11 - v} in lower faces)",
                (11 - v) in lowerValues
            )
        }
    }

    @Test
    fun `all vertices lie on circumsphere of radius sqrt 3 matching other dice`() {
        val expectedRadius = sqrt(3f)
        geo.vertices.forEach { v ->
            val radius = sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
            assertEquals("All vertices should lie on circumsphere of radius √3", expectedRadius, radius, 0.001f)
        }
    }

    @Test
    fun `all kite faces are planar`() {
        // A quadrilateral is planar iff (v3 - v0) is perpendicular to the face normal.
        // Non-planar faces (e.g. from wrong ring height) cause visual warping.
        geo.faces.forEach { face ->
            val v0 = geo.vertices[face.vertexIndices[0]]
            val v1 = geo.vertices[face.vertexIndices[1]]
            val v2 = geo.vertices[face.vertexIndices[2]]
            val v3 = geo.vertices[face.vertexIndices[3]]

            val normal = (v1 - v0).cross(v2 - v0).normalize()
            val deviation = abs(normal.dot(v3 - v0))

            assertTrue(
                "Face ${face.value} is not planar (deviation $deviation from plane)",
                deviation < 0.001f
            )
        }
    }

    @Test
    fun `getFaceRotation orients apex vertex to top of screen`() {
        geo.faces.forEach { face ->
            val (rx, ry, rz) = geo.getFaceRotation(face)

            val apex = geo.vertices[face.vertexIndices[0]]
            val faceVerts = face.vertexIndices.map { geo.vertices[it] }
            val center = faceVerts.reduce { acc, v -> acc + v } * (1f / faceVerts.size)

            val apexRotated = apex.rotatePoint(rx, ry, rz)
            val centerRotated = center.rotatePoint(rx, ry, rz)

            assertEquals(
                "Face ${face.value}: apex should be horizontally centered",
                centerRotated.x, apexRotated.x, 0.05f
            )
            assertTrue(
                "Face ${face.value}: apex should be above face center (smaller Y)",
                apexRotated.y < centerRotated.y
            )
        }
    }
}
