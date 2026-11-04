package com.greenfodor.diceroller.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** The date a [RollHistorySection] covers, relative to the "now" it was grouped against. */
sealed interface RollHistoryDay {
    /** The same calendar day as "now". */
    data object Today : RollHistoryDay

    /** The calendar day before "now". */
    data object Yesterday : RollHistoryDay

    /**
     * Any earlier calendar day. Carries the raw date so the presentation layer formats it
     * with the viewer's locale.
     */
    data class Earlier(
        val date: LocalDate
    ) : RollHistoryDay
}

/** A run of rolls that happened on the same calendar day, newest first. */
data class RollHistorySection(
    val day: RollHistoryDay,
    val rolls: List<RollRecord>
)

/**
 * Groups [rolls] into one section per calendar day in [timeZone], newest day first and newest
 * roll first within each day. The day of [now] becomes [RollHistoryDay.Today], the day before
 * it [RollHistoryDay.Yesterday], and every earlier day a [RollHistoryDay.Earlier].
 *
 * Rolls dated after [now] are grouped under their own calendar day like any other.
 */
fun groupRollsByDay(
    rolls: List<RollRecord>,
    now: Instant,
    timeZone: TimeZone
): List<RollHistorySection> {
    val today = now.toLocalDateTime(timeZone).date
    val yesterday = today.minus(1, DateTimeUnit.DAY)

    return rolls
        .sortedByDescending { it.timestampMillis }
        .groupBy { Instant.fromEpochMilliseconds(it.timestampMillis).toLocalDateTime(timeZone).date }
        .map { (date, dayRolls) ->
            val day = when (date) {
                today -> RollHistoryDay.Today
                yesterday -> RollHistoryDay.Yesterday
                else -> RollHistoryDay.Earlier(date)
            }
            RollHistorySection(day = day, rolls = dayRolls)
        }
}
