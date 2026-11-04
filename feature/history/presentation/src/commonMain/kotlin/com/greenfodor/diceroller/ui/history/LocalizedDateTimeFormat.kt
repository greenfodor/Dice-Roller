package com.greenfodor.diceroller.ui.history

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * A formatter that renders a [LocalDate] in the medium localized date style of the locale
 * identified by the BCP 47 [languageTag].
 */
internal expect fun mediumDateFormatter(languageTag: String): (LocalDate) -> String

/**
 * A formatter that renders a [LocalTime] in the short localized time style of the locale
 * identified by the BCP 47 [languageTag].
 */
internal expect fun shortTimeFormatter(languageTag: String): (LocalTime) -> String
