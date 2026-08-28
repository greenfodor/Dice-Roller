package com.greenfodor.diceroller.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class RollHistoryGroupingTest {
    private val zone = ZoneId.of("Europe/Bucharest")
    private val now: Instant = Instant.ofEpochMilli(millisAt(2026, 8, 28, 14, 30))

    @Test
    fun `empty history produces no sections`() {
        assertEquals(emptyList<RollHistorySection>(), groupRollsByDay(emptyList(), now, zone))
    }

    @Test
    fun `a roll on the same calendar day as now lands under Today`() {
        val sections = groupRollsByDay(listOf(rollAt(2026, 8, 28, 9, 0)), now, zone)

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Today), sections.map { it.day })
    }

    @Test
    fun `a roll on the calendar day before now lands under Yesterday`() {
        val sections = groupRollsByDay(listOf(rollAt(2026, 8, 27, 23, 59)), now, zone)

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Yesterday), sections.map { it.day })
    }

    @Test
    fun `a roll two days before now lands under Earlier carrying its own date`() {
        val sections = groupRollsByDay(listOf(rollAt(2026, 8, 26, 12, 0)), now, zone)

        assertEquals(
            listOf<RollHistoryDay>(RollHistoryDay.Earlier(LocalDate.of(2026, 8, 26))),
            sections.map { it.day }
        )
    }

    @Test
    fun `a roll at exactly midnight belongs to the new day`() {
        val sections = groupRollsByDay(listOf(rollAt(2026, 8, 28, 0, 0)), now, zone)

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Today), sections.map { it.day })
    }

    @Test
    fun `a roll one millisecond before midnight belongs to the previous day`() {
        val lastMillisOfYesterday = RollRecord(
            dieLabel = "d6",
            values = listOf(3),
            total = 3,
            timestampMillis = millisAt(2026, 8, 28, 0, 0) - 1
        )

        val sections = groupRollsByDay(listOf(lastMillisOfYesterday), now, zone)

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Yesterday), sections.map { it.day })
    }

    @Test
    fun `rolls spanning Today Yesterday and older are split into one section per day`() {
        val rolls = listOf(
            rollAt(2026, 8, 28, 10, 0),
            rollAt(2026, 8, 27, 10, 0),
            rollAt(2026, 8, 20, 10, 0)
        )

        val sections = groupRollsByDay(rolls, now, zone)

        assertEquals(
            listOf(
                RollHistoryDay.Today,
                RollHistoryDay.Yesterday,
                RollHistoryDay.Earlier(LocalDate.of(2026, 8, 20))
            ),
            sections.map { it.day }
        )
        assertTrue(sections.all { it.rolls.size == 1 })
    }

    @Test
    fun `several rolls on one day share a single section`() {
        val rolls = listOf(
            rollAt(2026, 8, 28, 12, 0),
            rollAt(2026, 8, 28, 11, 0),
            rollAt(2026, 8, 28, 10, 0)
        )

        val sections = groupRollsByDay(rolls, now, zone)

        assertEquals(1, sections.size)
        assertEquals(3, sections.single().rolls.size)
    }

    @Test
    fun `sections and their rolls come back newest first regardless of input order`() {
        val oldest = rollAt(2026, 8, 20, 10, 0)
        val middle = rollAt(2026, 8, 28, 9, 0)
        val newest = rollAt(2026, 8, 28, 13, 0)

        val sections = groupRollsByDay(listOf(oldest, newest, middle), now, zone)

        assertEquals(listOf(newest, middle), sections.first().rolls)
        assertEquals(listOf(oldest), sections.last().rolls)
    }

    @Test
    fun `grouping follows the supplied zone when it shifts the calendar day`() {
        val lateEvening = rollAt(2026, 8, 28, 23, 30)

        val bucharest = groupRollsByDay(listOf(lateEvening), now, zone)
        val tokyo = groupRollsByDay(listOf(lateEvening), now, ZoneId.of("Asia/Tokyo"))

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Today), bucharest.map { it.day })
        assertEquals(
            listOf<RollHistoryDay>(RollHistoryDay.Earlier(LocalDate.of(2026, 8, 29))),
            tokyo.map { it.day }
        )
    }

    @Test
    fun `rolls in different months are split into separate sections`() {
        val rolls = listOf(
            rollAt(2026, 8, 1, 9, 0),
            rollAt(2026, 7, 31, 9, 0),
            rollAt(2026, 6, 15, 9, 0)
        )

        val sections = groupRollsByDay(rolls, now, zone)

        assertEquals(
            listOf(
                RollHistoryDay.Earlier(LocalDate.of(2026, 8, 1)),
                RollHistoryDay.Earlier(LocalDate.of(2026, 7, 31)),
                RollHistoryDay.Earlier(LocalDate.of(2026, 6, 15))
            ),
            sections.map { it.day }
        )
    }

    @Test
    fun `rolls in different years are split into separate sections`() {
        val rolls = listOf(
            rollAt(2025, 12, 31, 23, 0),
            rollAt(2024, 8, 28, 14, 30),
            rollAt(2019, 1, 1, 0, 0)
        )

        val sections = groupRollsByDay(rolls, now, zone)

        assertEquals(
            listOf(
                RollHistoryDay.Earlier(LocalDate.of(2025, 12, 31)),
                RollHistoryDay.Earlier(LocalDate.of(2024, 8, 28)),
                RollHistoryDay.Earlier(LocalDate.of(2019, 1, 1))
            ),
            sections.map { it.day }
        )
    }

    @Test
    fun `the same day and month in an earlier year is not mistaken for Today`() {
        val sections = groupRollsByDay(listOf(rollAt(2025, 8, 28, 14, 30)), now, zone)

        assertEquals(
            listOf<RollHistoryDay>(RollHistoryDay.Earlier(LocalDate.of(2025, 8, 28))),
            sections.map { it.day }
        )
    }

    @Test
    fun `Yesterday spanning a month boundary is still labelled Yesterday`() {
        val septemberFirst = Instant.ofEpochMilli(millisAt(2026, 9, 1, 8, 0))

        val sections = groupRollsByDay(listOf(rollAt(2026, 8, 31, 22, 0)), septemberFirst, zone)

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Yesterday), sections.map { it.day })
    }

    @Test
    fun `Yesterday spanning a year boundary is still labelled Yesterday`() {
        val newYearsDay = Instant.ofEpochMilli(millisAt(2027, 1, 1, 0, 30))

        val sections = groupRollsByDay(listOf(rollAt(2026, 12, 31, 23, 45)), newYearsDay, zone)

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Yesterday), sections.map { it.day })
    }

    @Test
    fun `Yesterday spanning a leap day is still labelled Yesterday`() {
        val firstOfMarch = Instant.ofEpochMilli(millisAt(2028, 3, 1, 10, 0))

        val sections = groupRollsByDay(listOf(rollAt(2028, 2, 29, 20, 0)), firstOfMarch, zone)

        assertEquals(listOf<RollHistoryDay>(RollHistoryDay.Yesterday), sections.map { it.day })
    }

    @Test
    fun `rolls sharing a day and month but not a year stay in separate sections`() {
        val rolls = listOf(rollAt(2026, 3, 14, 9, 0), rollAt(2025, 3, 14, 9, 0))

        val sections = groupRollsByDay(rolls, now, zone)

        assertEquals(2, sections.size)
        assertTrue(sections.all { it.rolls.size == 1 })
    }

    @Test
    fun `a multi die roll keeps every value and its scored total`() {
        val percentile = RollRecord(
            dieLabel = "d100",
            values = listOf(90, 0),
            total = 90,
            timestampMillis = millisAt(2026, 8, 28, 8, 0)
        )

        val stored = groupRollsByDay(listOf(percentile), now, zone).single().rolls.single()

        assertEquals(listOf(90, 0), stored.values)
        assertEquals(90, stored.total)
    }

    private fun millisAt(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        LocalDateTime.of(year, month, day, hour, minute).atZone(zone).toInstant().toEpochMilli()

    private fun rollAt(year: Int, month: Int, day: Int, hour: Int, minute: Int): RollRecord =
        RollRecord(
            dieLabel = "d6",
            values = listOf(4),
            total = 4,
            timestampMillis = millisAt(year, month, day, hour, minute)
        )
}
