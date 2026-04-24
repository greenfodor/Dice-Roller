package com.greenfodor.diceroller.geometry

import org.junit.Assert.assertEquals
import org.junit.Test

class DiceInterpolationTest {
    private val faceVertices =
        listOf(
            Point3D(-10f, -10f, 0f), // v0: Top-Left (-1, -1 in UV)
            Point3D(10f, -10f, 0f), // v1: Top-Right ( 1, -1 in UV)
            Point3D(10f, 10f, 0f), // v2: Bottom-Right ( 1,  1 in UV)
            Point3D(-10f, 10f, 0f) // v3: Bottom-Left (-1,  1 in UV)
        )

    @Test
    fun `center of face (0,0) interpolates correctly`() {
        val result = interpolatePoint3DOnFace(u = 0f, v = 0f, vVertices = faceVertices)
        assertEquals(Point3D(0f, 0f, 0f), result)
    }

    @Test
    fun `corners interpolate correctly`() {
        // UV (-1, -1) -> v0
        assertEquals(Point3D(-10f, -10f, 0f), interpolatePoint3DOnFace(-1f, -1f, faceVertices))

        // UV (1, -1) -> v1
        assertEquals(Point3D(10f, -10f, 0f), interpolatePoint3DOnFace(1f, -1f, faceVertices))

        // UV (1, 1) -> v2
        assertEquals(Point3D(10f, 10f, 0f), interpolatePoint3DOnFace(1f, 1f, faceVertices))

        // UV (-1, 1) -> v3
        assertEquals(Point3D(-10f, 10f, 0f), interpolatePoint3DOnFace(-1f, 1f, faceVertices))
    }

    @Test
    fun `normal offset is applied correctly`() {
        val offset = Point3D(0f, 0f, 5f)
        val result = interpolatePoint3DOnFace(u = 0f, v = 0f, vVertices = faceVertices, normalOffset = offset)
        assertEquals(Point3D(0f, 0f, 5f), result)
    }
}
