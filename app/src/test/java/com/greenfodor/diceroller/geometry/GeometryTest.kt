package com.greenfodor.diceroller.geometry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeometryTest {
    @Test
    fun `test Point3D vector operations`() {
        val p1 = Point3D(1f, 2f, 3f)
        val p2 = Point3D(4f, 5f, 6f)

        // Addition
        assertEquals(Point3D(5f, 7f, 9f), p1 + p2)

        // Subtraction
        assertEquals(Point3D(-3f, -3f, -3f), p1 - p2)

        // Multiplication
        assertEquals(Point3D(2f, 4f, 6f), p1 * 2f)

        // Dot product
        assertEquals(32f, p1.dot(p2))

        // Normalization
        val p3 = Point3D(3f, 0f, 0f)
        assertEquals(Point3D(1f, 0f, 0f), p3.normalize())
    }

    @Test
    fun `test cross product for face normals`() {
        // unit vectors along X and Y
        val vX = Point3D(1f, 0f, 0f)
        val vY = Point3D(0f, 1f, 0f)

        // Cross product of X and Y should be Z
        val vZ = vX.cross(vY)
        assertEquals(Point3D(0f, 0f, 1f), vZ)
    }

    @Test
    fun `test rotation at 0 degrees returns same point`() {
        val p = Point3D(10f, 20f, 30f)
        val rotated = p.rotatePoint(0f, 0f)

        assertEquals(p.x, rotated.x, 0.001f)
        assertEquals(p.y, rotated.y, 0.001f)
        assertEquals(p.z, rotated.z, 0.001f)
    }

    @Test
    fun `test rotation around Y axis`() {
        val p = Point3D(100f, 0f, 0f)
        // Rotate 90 degrees around Y
        val rotated = p.rotatePoint(0f, 90f)

        assertEquals(0f, rotated.x, 0.01f)
        assertEquals(0f, rotated.y, 0.01f)
        assertEquals(-100f, rotated.z, 0.01f)
    }

    @Test
    fun `test perspective projection`() {
        // Point at center of coordinate system
        val p = Point3D(0f, 0f, 0f)
        val centerX = 500f
        val centerY = 500f

        val projected = p.projectPoint(centerX, centerY)

        // Should be exactly at the center
        assertEquals(centerX, projected.x, 0.001f)
        assertEquals(centerY, projected.y, 0.001f)

        // Point further away (smaller factor)
        val pFar = Point3D(10f, 0f, -100f)
        val projectedFar = pFar.projectPoint(centerX, centerY)

        // Factor = 500 / (800 - (-100)) = 500 / 900 = 0.555...
        // x = 500 + 10 * 0.555 = 505.555...
        assertTrue(projectedFar.x > centerX)
        assertTrue(projectedFar.x < centerX + 10f)
    }

    @Test
    fun `calculateNormalZ positive when face points toward camera`() {
        val v0 = Point3D(0f, 0f, 0f)
        val v1 = Point3D(1f, 0f, 0f)
        val v3 = Point3D(0f, 1f, 0f)
        assertTrue(calculateNormalZ(v0, v1, v3) > 0f)
    }

    @Test
    fun `calculateNormalZ negative for reversed winding`() {
        val v0 = Point3D(0f, 0f, 0f)
        val v1 = Point3D(0f, 1f, 0f)
        val v3 = Point3D(1f, 0f, 0f)
        assertTrue(calculateNormalZ(v0, v1, v3) < 0f)
    }

    @Test
    fun `calculateNormalZ zero for collinear points`() {
        val v0 = Point3D(0f, 0f, 0f)
        val v1 = Point3D(1f, 0f, 0f)
        val v3 = Point3D(2f, 0f, 0f)
        assertEquals(0f, calculateNormalZ(v0, v1, v3), 0.001f)
    }

    @Test
    fun `test back-face culling normal calculation for all faces`() {
        CubeGeometry.faces.forEach { face ->
            val v0 = CubeGeometry.vertices[face.vertexIndices[0]]
            val v1 = CubeGeometry.vertices[face.vertexIndices[1]]
            val v3 = CubeGeometry.vertices[face.vertexIndices[3]]

            val normal = (v1 - v0).cross(v3 - v0)

            // For each face, the normal should point OUTWARD from the origin (0,0,0)
            // So dot product of normal and any vertex on that face should be positive.
            assertTrue(
                "Face ${face.value} normal $normal should point outward",
                normal.dot(v0) > 0
            )
        }
    }
}
