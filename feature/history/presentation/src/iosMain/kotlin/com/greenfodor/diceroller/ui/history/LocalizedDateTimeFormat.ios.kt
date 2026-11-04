package com.greenfodor.diceroller.ui.history

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSDateFormatterStyle
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.canonicalLocaleIdentifierFromString
import platform.Foundation.timeZoneForSecondsFromGMT

/** The calendar day a [LocalTime] is placed on before it is formatted. */
private val TIME_REFERENCE_DATE = LocalDate(2000, 1, 1)

internal actual fun mediumDateFormatter(languageTag: String): (LocalDate) -> String {
    val formatter =
        utcDateFormatter(languageTag, dateStyle = NSDateFormatterMediumStyle, timeStyle = NSDateFormatterNoStyle)
    return { date -> formatter.stringFromDate(date.atStartOfDayIn(TimeZone.UTC).toNSDate()) }
}

internal actual fun shortTimeFormatter(languageTag: String): (LocalTime) -> String {
    val formatter =
        utcDateFormatter(languageTag, dateStyle = NSDateFormatterNoStyle, timeStyle = NSDateFormatterShortStyle)
    return { time -> formatter.stringFromDate(TIME_REFERENCE_DATE.atTime(time).toInstant(TimeZone.UTC).toNSDate()) }
}

/** An [NSDateFormatter] for the locale identified by [languageTag] that renders instants in UTC. */
private fun utcDateFormatter(
    languageTag: String,
    dateStyle: NSDateFormatterStyle,
    timeStyle: NSDateFormatterStyle
): NSDateFormatter = NSDateFormatter().apply {
    locale = NSLocale(localeIdentifier = NSLocale.canonicalLocaleIdentifierFromString(languageTag))
    timeZone = NSTimeZone.timeZoneForSecondsFromGMT(0)
    this.dateStyle = dateStyle
    this.timeStyle = timeStyle
}
