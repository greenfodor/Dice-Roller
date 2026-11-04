package com.greenfodor.diceroller.ui.history

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedDateTimeFormatTest {
    @Test
    fun `a date renders in the medium date style of the locale`() {
        assertEquals("Aug 20, 2026", mediumDateFormatter("en-US")(DATE))
        assertEquals("20.08.2026", mediumDateFormatter("de-DE")(DATE))
    }

    @Test
    fun `a time renders in the short time style of the locale`() {
        assertEquals("2:30\u202FPM", shortTimeFormatter("en-US")(TIME))
        assertEquals("14:30", shortTimeFormatter("de-DE")(TIME))
    }

    private companion object {
        val DATE = LocalDate(2026, 8, 20)
        val TIME = LocalTime(14, 30)
    }
}
