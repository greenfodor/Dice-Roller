package com.greenfodor.diceroller.data.history

import org.junit.Assert.assertEquals
import org.junit.Test

class IntListConverterTest {
    private val converter = IntListConverter()

    @Test
    fun `an empty list is stored as an empty string`() {
        assertEquals("", converter.fromIntList(emptyList()))
    }

    @Test
    fun `an empty string is read back as an empty list`() {
        assertEquals(emptyList<Int>(), converter.toIntList(""))
    }

    @Test
    fun `a single value round-trips`() {
        assertEquals(listOf(6), converter.toIntList(converter.fromIntList(listOf(6))))
    }

    @Test
    fun `several values round-trip in order`() {
        val values = listOf(4, 6, 1, 20)

        assertEquals(values, converter.toIntList(converter.fromIntList(values)))
    }

    @Test
    fun `a zero value round-trips`() {
        assertEquals(listOf(0, 0), converter.toIntList(converter.fromIntList(listOf(0, 0))))
    }

    @Test
    fun `a trailing separator is read back without a missing value`() {
        assertEquals(listOf(3), converter.toIntList("3,"))
    }

    @Test
    fun `a non-numeric part is dropped`() {
        assertEquals(listOf(3), converter.toIntList("3,a"))
    }

    @Test
    fun `a wholly non-numeric string is read back as an empty list`() {
        assertEquals(emptyList<Int>(), converter.toIntList("a"))
    }
}
