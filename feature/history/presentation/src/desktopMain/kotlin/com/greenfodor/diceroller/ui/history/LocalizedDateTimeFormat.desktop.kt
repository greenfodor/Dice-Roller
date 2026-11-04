package com.greenfodor.diceroller.ui.history

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

internal actual fun mediumDateFormatter(languageTag: String): (LocalDate) -> String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.forLanguageTag(languageTag))
    return { date -> formatter.format(date.toJavaLocalDate()) }
}

internal actual fun shortTimeFormatter(languageTag: String): (LocalTime) -> String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.forLanguageTag(languageTag))
    return { time -> formatter.format(time.toJavaLocalTime()) }
}
