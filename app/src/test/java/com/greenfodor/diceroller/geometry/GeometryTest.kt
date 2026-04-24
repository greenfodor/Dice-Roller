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
    fun `test back-face culling normal calculation for all faces`() {
        val vertices = listOf(
            Point3D(-1f, -1f, -1f), // 0
            Point3D(1f, -1f, -1f),  // 1
            Point3D(1f, 1f, -1f),   // 2
            Point3D(-1f, 1f, -1f),  // 3
            Point3D(-1f, -1f, 1f),  // 4
            Point3D(1f, -1f, 1f),   // 5
            Point3D(1f, 1f, 1f),    // 6
            Point3D(-1f, 1f, 1f)    // 7
        )

        // Face definitions (must match CubeRenderer.kt)
        val faceIndices = listOf(
            listOf(4, 5, 6, 7), // Front  (Z+)
            listOf(1, 0, 3, 2), // Back   (Z-)
            listOf(0, 1, 5, 4), // Bottom (Y-)
            listOf(7, 6, 2, 3), // Top    (Y+)
            listOf(0, 4, 7, 3), // Left   (X-)
            listOf(5, 1, 2, 6)  // Right  (X+)
        )

        val faceNames = listOf("Front", "Back", "Bottom", "Top", "Left", "Right")

        faceIndices.forEachIndexed { index, indices ->
            val v0 = vertices[indices[0]]
            val v1 = vertices[indices[1]]
            val v3 = vertices[indices[3]]

            val normal = (v1 - v0).cross(v3 - v0)
            
            // For each face, the normal should point OUTWARD from the origin (0,0,0)
            // So dot product of normal and any vertex on that face should be positive.
            assertTrue(
                "Face ${faceNames[index]} normal $normal should point outward",
                normal.dot(v0) > 0
            )
        }
    }
}
