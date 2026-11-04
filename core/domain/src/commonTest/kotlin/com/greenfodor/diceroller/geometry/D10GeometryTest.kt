package com.greenfodor.diceroller.geometry

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        assertEquals(
            (1..10).toList(),
            values,
            "Should have 10 faces"
        )
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
                normal.dot(center) > 0,
                "Face ${face.value} normal should point outward (dot product: ${normal.dot(center)})"
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

            assertEquals(
                0f,
                rotatedNormal.x,
                0.01f,
                "Face ${face.value}: Normal X should be 0"
            )
            assertEquals(
                0f,
                rotatedNormal.y,
                0.01f,
                "Face ${face.value}: Normal Y should be 0"
            )
            assertTrue(
                rotatedNormal.z > 0.99f,
                "Face ${face.value}: Normal Z should be positive"
            )
        }
    }

    @Test
    fun `opposite faces sum to 11`() {
        val upperFaces = geo.faces.filter { it.vertexIndices[0] == 0 }
        val lowerFaces = geo.faces.filter { it.vertexIndices[0] == 11 }

        assertEquals(
            5,
            upperFaces.size,
            "Should have 5 upper faces"
        )
        assertEquals(
            5,
            lowerFaces.size,
            "Should have 5 lower faces"
        )

        val upperValues = upperFaces.map { it.value }.toSet()
        val lowerValues = lowerFaces.map { it.value }.toSet()

        upperValues.forEach { v ->
            assertTrue(
                (11 - v) in lowerValues,
                "Opposite of $v should sum to 11 (${11 - v} in lower faces)"
            )
        }
    }

    @Test
    fun `all vertices lie on circumsphere of radius sqrt 3 matching other dice`() {
        val expectedRadius = sqrt(3f)
        geo.vertices.forEach { v ->
            val radius = sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
            assertEquals(
                expectedRadius,
                radius,
                0.001f,
                "All vertices should lie on circumsphere of radius √3"
            )
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
                deviation < 0.001f,
                "Face ${face.value} is not planar (deviation $deviation from plane)"
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
                centerRotated.x,
                apexRotated.x,
                0.05f,
                "Face ${face.value}: apex should be horizontally centered"
            )
            assertTrue(
                apexRotated.y < centerRotated.y,
                "Face ${face.value}: apex should be above face center (smaller Y)"
            )
        }
    }
}
