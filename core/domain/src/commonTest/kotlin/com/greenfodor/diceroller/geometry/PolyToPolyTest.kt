package com.greenfodor.diceroller.geometry

import com.greenfodor.diceroller.ui.DiceConstants
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PolyToPolyTest {
    private val tolerance = 1e-4f
    private val unitTriangle = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f)
    private val unitSquare = floatArrayOf(0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f)

    private fun affine(src: FloatArray, dst: FloatArray): FloatArray {
        val out = FloatArray(AFFINE_COEFFICIENT_COUNT)
        assertTrue(triangleToTriangleTransform(src, dst, out))
        return out
    }

    private fun projective(src: FloatArray, dst: FloatArray): FloatArray {
        val out = FloatArray(PROJECTIVE_COEFFICIENT_COUNT)
        assertTrue(quadToQuadTransform(src, dst, out))
        return out
    }

    private fun assertCoefficients(expected: FloatArray, actual: FloatArray) {
        assertEquals(expected.size, actual.size)
        expected.forEachIndexed { i, value ->
            assertEquals(value, actual[i], tolerance, "coefficient $i of ${actual.contentToString()}")
        }
    }

    private fun mapAffine(m: FloatArray, x: Float, y: Float) = Point2D(
        m[COEFF_SCALE_X] * x + m[COEFF_SKEW_X] * y + m[COEFF_TRANSLATE_X],
        m[COEFF_SKEW_Y] * x + m[COEFF_SCALE_Y] * y + m[COEFF_TRANSLATE_Y]
    )

    private fun mapProjective(m: FloatArray, x: Float, y: Float): Point2D {
        val w = m[COEFF_PERSPECTIVE_X] * x + m[COEFF_PERSPECTIVE_Y] * y + m[COEFF_PERSPECTIVE_W]
        return Point2D(
            (m[COEFF_SCALE_X] * x + m[COEFF_SKEW_X] * y + m[COEFF_TRANSLATE_X]) / w,
            (m[COEFF_SKEW_Y] * x + m[COEFF_SCALE_Y] * y + m[COEFF_TRANSLATE_Y]) / w
        )
    }

    private fun assertPoint(expectedX: Float, expectedY: Float, actual: Point2D) {
        assertEquals(expectedX, actual.x, tolerance, "x of $actual")
        assertEquals(expectedY, actual.y, tolerance, "y of $actual")
    }

    @Test
    fun `identical triangles give the identity`() {
        val triangle = floatArrayOf(3f, 4f, 10f, -2f, -5f, 7f)
        assertCoefficients(floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f), affine(triangle, triangle))
    }

    @Test
    fun `shifted triangle gives a translation`() {
        val dst = floatArrayOf(5f, -3f, 6f, -3f, 5f, -2f)
        assertCoefficients(floatArrayOf(1f, 0f, 5f, 0f, 1f, -3f), affine(unitTriangle, dst))
    }

    @Test
    fun `stretched triangle gives a scale`() {
        val dst = floatArrayOf(0f, 0f, 2f, 0f, 0f, 3f)
        assertCoefficients(floatArrayOf(2f, 0f, 0f, 0f, 3f, 0f), affine(unitTriangle, dst))
    }

    @Test
    fun `quarter turned triangle gives a rotation`() {
        val dst = floatArrayOf(0f, 0f, 0f, 1f, -1f, 0f)
        assertCoefficients(floatArrayOf(0f, -1f, 0f, 1f, 0f, 0f), affine(unitTriangle, dst))
    }

    @Test
    fun `slanted triangle gives a shear`() {
        val dst = floatArrayOf(0f, 0f, 1f, 0f, 2f, 1f)
        assertCoefficients(floatArrayOf(1f, 2f, 0f, 0f, 1f, 0f), affine(unitTriangle, dst))
    }

    @Test
    fun `rotation scale and translation combine around a non origin source`() {
        val src = floatArrayOf(1f, 1f, 3f, 1f, 1f, 2f)
        val dst = floatArrayOf(10f, 20f, 10f, 24f, 8f, 20f)
        assertCoefficients(floatArrayOf(0f, -2f, 12f, 2f, 0f, 18f), affine(src, dst))
    }

    @Test
    fun `d20 source triangle maps every corner onto the face`() {
        val dst = floatArrayOf(120f, 40f, 180f, 150f, 70f, 140f)
        val m = affine(DiceConstants.D20_SRC_TRIANGLE, dst)
        for (i in 0 until 3) {
            val mapped = mapAffine(m, DiceConstants.D20_SRC_TRIANGLE[i * 2], DiceConstants.D20_SRC_TRIANGLE[i * 2 + 1])
            assertPoint(dst[i * 2], dst[i * 2 + 1], mapped)
        }
    }

    @Test
    fun `collinear source triangle is rejected and leaves the output untouched`() {
        val out = FloatArray(AFFINE_COEFFICIENT_COUNT) { 7f }
        val collinear = floatArrayOf(0f, 0f, 1f, 1f, 2f, 2f)
        assertFalse(triangleToTriangleTransform(collinear, unitTriangle, out))
        assertContentEquals(FloatArray(AFFINE_COEFFICIENT_COUNT) { 7f }, out)
    }

    @Test
    fun `coincident source points are rejected`() {
        val out = FloatArray(AFFINE_COEFFICIENT_COUNT)
        assertFalse(triangleToTriangleTransform(FloatArray(6) { 4f }, unitTriangle, out))
        assertFalse(triangleToTriangleTransform(floatArrayOf(0f, 0f, 0f, 0f, 1f, 1f), unitTriangle, out))
    }

    @Test
    fun `nearly collinear source within tolerance is rejected`() {
        val out = FloatArray(AFFINE_COEFFICIENT_COUNT)
        val almostFlat = floatArrayOf(0f, 0f, 100f, 0f, 50f, 1e-5f)
        assertFalse(triangleToTriangleTransform(almostFlat, unitTriangle, out))
    }

    @Test
    fun `collinear destination gives a singular transform`() {
        val dst = floatArrayOf(0f, 0f, 1f, 1f, 2f, 2f)
        val m = affine(unitTriangle, dst)
        assertCoefficients(floatArrayOf(1f, 2f, 0f, 1f, 2f, 0f), m)
        val determinant = m[COEFF_SCALE_X] * m[COEFF_SCALE_Y] - m[COEFF_SKEW_X] * m[COEFF_SKEW_Y]
        assertEquals(0f, determinant, tolerance)
    }

    @Test
    fun `identical quads give the identity`() {
        val quad = floatArrayOf(-3f, -2f, 5f, -1f, 6f, 4f, -2f, 3f)
        assertCoefficients(floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f), projective(quad, quad))
    }

    @Test
    fun `parallelogram destination gives an affine homography`() {
        val dst = floatArrayOf(5f, 5f, 7f, 5f, 9f, 8f, 7f, 8f)
        assertCoefficients(floatArrayOf(2f, 2f, 5f, 0f, 3f, 5f, 0f, 0f, 1f), projective(unitSquare, dst))
    }

    @Test
    fun `trapezoid destination gives a perspective homography`() {
        val dst = floatArrayOf(0f, 0f, 2f, 0f, 1.5f, 1f, 0.5f, 1f)
        val m = projective(unitSquare, dst)
        assertCoefficients(floatArrayOf(2f, 1f, 0f, 0f, 2f, 0f, 0f, 1f, 1f), m)
        assertPoint(1f, 2f / 3f, mapProjective(m, 0.5f, 0.5f))
    }

    @Test
    fun `trapezoid source maps back onto the unit square`() {
        val src = floatArrayOf(0f, 0f, 2f, 0f, 1.5f, 1f, 0.5f, 1f)
        val m = projective(src, unitSquare)
        for (i in 0 until 4) {
            assertPoint(unitSquare[i * 2], unitSquare[i * 2 + 1], mapProjective(m, src[i * 2], src[i * 2 + 1]))
        }
        assertPoint(0.5f, 0.5f, mapProjective(m, 1f, 2f / 3f))
    }

    @Test
    fun `d6 source quad maps every corner onto a projected face`() {
        val dst = floatArrayOf(210f, 230f, 90f, 215f, 105f, 95f, 200f, 120f)
        val m = projective(DiceConstants.D6_SRC_QUAD, dst)
        for (i in 0 until 4) {
            val mapped = mapProjective(m, DiceConstants.D6_SRC_QUAD[i * 2], DiceConstants.D6_SRC_QUAD[i * 2 + 1])
            assertPoint(dst[i * 2], dst[i * 2 + 1], mapped)
        }
    }

    @Test
    fun `quad with three collinear source points is rejected and leaves the output untouched`() {
        val out = FloatArray(PROJECTIVE_COEFFICIENT_COUNT) { 7f }
        val src = floatArrayOf(0f, 0f, 1f, 0f, 2f, 0f, 0f, 1f)
        assertFalse(quadToQuadTransform(src, unitSquare, out))
        assertContentEquals(FloatArray(PROJECTIVE_COEFFICIENT_COUNT) { 7f }, out)
    }

    @Test
    fun `quad with three collinear destination points is rejected`() {
        val out = FloatArray(PROJECTIVE_COEFFICIENT_COUNT)
        val dst = floatArrayOf(0f, 0f, 1f, 1f, 1f, 2f, 2f, 2f)
        assertFalse(quadToQuadTransform(unitSquare, dst, out))
        assertFalse(quadToQuadTransform(unitSquare, FloatArray(8), out))
    }
}
