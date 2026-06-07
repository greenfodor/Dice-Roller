package com.greenfodor.diceroller.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeModeTest {
    @Test
    fun `fromName maps each enum name back to its value`() {
        ThemeMode.entries.forEach { mode ->
            assertEquals(mode, ThemeMode.fromName(mode.name))
        }
    }

    @Test
    fun `fromName falls back to FOLLOW_SYSTEM for null, blank, unknown, or wrong-case input`() {
        assertEquals(ThemeMode.FOLLOW_SYSTEM, ThemeMode.fromName(null))
        assertEquals(ThemeMode.FOLLOW_SYSTEM, ThemeMode.fromName(""))
        assertEquals(ThemeMode.FOLLOW_SYSTEM, ThemeMode.fromName("nonsense"))
        assertEquals(ThemeMode.FOLLOW_SYSTEM, ThemeMode.fromName("dark"))
    }
}
