package com.greenfodor.diceroller.ui.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.feature.history.presentation.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale as JavaLocale

/** The header text for [day]: `Today`, `Yesterday`, or the date in the viewer's locale. */
@Composable
internal fun rollHistoryDayLabel(day: RollHistoryDay): String = when (day) {
    RollHistoryDay.Today -> stringResource(R.string.roll_history_today)
    RollHistoryDay.Yesterday -> stringResource(R.string.roll_history_yesterday)
    is RollHistoryDay.Earlier -> {
        val locale = Locale.current
        val formatter = remember(locale) {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale.toJavaLocale())
        }
        formatter.format(day.date)
    }
}

/**
 * The secondary line of a history row: the time the roll happened in [zoneId], preceded by the
 * individual die values when the roll used more than one die.
 */
@Composable
internal fun rollHistoryDetails(record: RollRecord, zoneId: ZoneId): String {
    val locale = Locale.current
    val formatter = remember(locale) {
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale.toJavaLocale())
    }
    val time = remember(record.timestampMillis, formatter, zoneId) {
        formatter.format(Instant.ofEpochMilli(record.timestampMillis).atZone(zoneId).toLocalTime())
    }

    if (record.values.size <= 1) return time

    val values = record.values.joinToString(separator = stringResource(R.string.roll_history_value_separator))
    return stringResource(R.string.roll_history_row_details, values, time)
}

private fun Locale.toJavaLocale(): JavaLocale = JavaLocale.forLanguageTag(toLanguageTag())
