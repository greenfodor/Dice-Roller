package com.greenfodor.diceroller.ui.dice.d100

import com.greenfodor.diceroller.geometry.PentagonalTrapezohedronGeometry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PercentileDiceTest {
    @Test
    fun `units labels read 0 through 9 with 10 mapping to 0`() {
        assertEquals("1", unitsLabel(1))
        assertEquals("9", unitsLabel(9))
        assertEquals("0", unitsLabel(10))

        val labels = geometryValues.map { unitsLabel(it) }.toSet()
        assertEquals((0..9).map { it.toString() }.toSet(), labels)
    }

    @Test
    fun `tens labels read 00 through 90 with 10 mapping to 00`() {
        assertEquals("10", tensLabel(1))
        assertEquals("90", tensLabel(9))
        assertEquals("00", tensLabel(10))

        val labels = geometryValues.map { tensLabel(it) }.toSet()
        assertEquals(setOf("00", "10", "20", "30", "40", "50", "60", "70", "80", "90"), labels)
    }

    @Test
    fun `units die scores cover 0 through 9 without duplicates`() {
        val values = PercentileUnitsDie.faces.map { it.value }
        assertEquals("Should have 10 faces", 10, values.size)
        assertEquals("Units scores should be 0..9", (0..9).toSet(), values.toSet())
    }

    @Test
    fun `tens die scores are the multiples of ten from 0 to 90 without duplicates`() {
        val values = PercentileTensDie.faces.map { it.value }
        assertEquals("Should have 10 faces", 10, values.size)
        assertEquals("Tens scores should be 0,10,..,90", (0..9).map { it * 10 }.toSet(), values.toSet())
    }

    @Test
    fun `each die face score matches the label rendered on that face`() {
        // The renderer derives a face's label from its geometry value via tensLabel/unitsLabel,
        // while each DieFace carries the scored contribution. They must agree, or the pips/label
        // shown would not match the value that lands (same class of bug as the D6 3/4 swap).
        PentagonalTrapezohedronGeometry.faces.forEachIndexed { index, geometryFace ->
            assertEquals(
                "Tens face $index score should equal its label",
                tensLabel(geometryFace.value).toInt(),
                PercentileTensDie.faces[index].value
            )
            assertEquals(
                "Units face $index score should equal its label",
                unitsLabel(geometryFace.value).toInt(),
                PercentileUnitsDie.faces[index].value
            )
        }
    }

    @Test
    fun `double zero reads as 100`() {
        assertEquals(100, percentileValue(0))
    }

    @Test
    fun `non-zero sums pass through unchanged`() {
        assertEquals(70, percentileValue(70))
        assertEquals(5, percentileValue(5))
        assertEquals(99, percentileValue(99))
    }

    @Test
    fun `the two dice together cover every result 1 through 100 exactly once`() {
        val tens = PercentileTensDie.faces.map { it.value }
        val units = PercentileUnitsDie.faces.map { it.value }

        val results = tens.flatMap { t -> units.map { u -> percentileValue(t + u) } }

        assertEquals("100 tens/units combinations", 100, results.size)
        assertEquals("Each combination is a distinct result", 100, results.toSet().size)
        assertEquals("Results cover 1..100", (1..100).toSet(), results.toSet())
    }

    private val geometryValues = PentagonalTrapezohedronGeometry.faces.map { it.value }

    @Test
    fun `geometry values span 1 through 10`() {
        // Guards the assumption the label/score mapping relies on.
        assertTrue("Geometry values should be 1..10", geometryValues.toSet() == (1..10).toSet())
    }
}
