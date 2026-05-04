package com.greenfodor.diceroller.geometry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class D20GeometryTest {
    @Test
    fun `all D20 faces have outward pointing normals`() {
        IcosahedronGeometry.faces.forEach { face ->
            val v0 = IcosahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = IcosahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = IcosahedronGeometry.vertices[face.vertexIndices[2]]

            // Normal calculated with CCW winding
            val normal = (v1 - v0).cross(v2 - v0)

            // For a convex shape centered at origin,
            // the dot product of the normal and the face center should be positive.
            val center = (v0 + v1 + v2) * (1f / 3f)
            assertTrue(
                "Face ${face.value} normal should point outward (dot product: ${normal.dot(center)})",
                normal.dot(center) > 0
            )
        }
    }

    @Test
    fun `getFaceRotation brings target face normal to camera direction`() {
        IcosahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = IcosahedronGeometry.getFaceRotation(face)

            val v0 = IcosahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = IcosahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = IcosahedronGeometry.vertices[face.vertexIndices[2]]

            val normal = (v1 - v0).cross(v2 - v0).normalize()

            // Rotate the normal by the calculated angles
            val rotatedNormal = normal.rotatePoint(rx, ry, rz)

            // Rotated normal should point towards +Z (camera direction)
            // with X and Y components being effectively 0
            assertEquals("Face ${face.value}: Normal X should be 0", 0f, rotatedNormal.x, 0.01f)
            assertEquals("Face ${face.value}: Normal Y should be 0", 0f, rotatedNormal.y, 0.01f)
            assertTrue("Face ${face.value}: Normal Z should be positive", rotatedNormal.z > 0.99f)
        }
    }

    @Test
    fun `getFaceRotation orients first vertex to top of screen`() {
        IcosahedronGeometry.faces.forEach { face ->
            val (rx, ry, rz) = IcosahedronGeometry.getFaceRotation(face)

            val v0 = IcosahedronGeometry.vertices[face.vertexIndices[0]]
            val v1 = IcosahedronGeometry.vertices[face.vertexIndices[1]]
            val v2 = IcosahedronGeometry.vertices[face.vertexIndices[2]]
            val vCenter = (v0 + v1 + v2) * (1f / 3f)

            // Rotate both first vertex and face center
            val v0Rotated = v0.rotatePoint(rx, ry, rz)
            val centerRotated = vCenter.rotatePoint(rx, ry, rz)

            // In screen space (+Y is down), "up" relative to the center means
            // the vertex should have the same X as center and a smaller Y.
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
