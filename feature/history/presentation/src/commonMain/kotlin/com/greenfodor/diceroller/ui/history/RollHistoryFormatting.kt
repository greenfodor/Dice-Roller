package com.greenfodor.diceroller.ui.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.feature.history.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.history.presentation.generated.resources.roll_history_row_details
import com.greenfodor.diceroller.feature.history.presentation.generated.resources.roll_history_today
import com.greenfodor.diceroller.feature.history.presentation.generated.resources.roll_history_value_separator
import com.greenfodor.diceroller.feature.history.presentation.generated.resources.roll_history_yesterday
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

/** The header text for [day]: `Today`, `Yesterday`, or the date in the viewer's locale. */
@Composable
internal fun rollHistoryDayLabel(day: RollHistoryDay): String = when (day) {
    RollHistoryDay.Today -> stringResource(Res.string.roll_history_today)
    RollHistoryDay.Yesterday -> stringResource(Res.string.roll_history_yesterday)
    is RollHistoryDay.Earlier -> {
        val locale = Locale.current
        val formatter = remember(locale) { mediumDateFormatter(locale.toLanguageTag()) }
        formatter(day.date)
    }
}

/**
 * The secondary line of a history row: the time the roll happened in [timeZone], preceded by the
 * individual die values when the roll used more than one die.
 */
@Composable
internal fun rollHistoryDetails(record: RollRecord, timeZone: TimeZone): String {
    val locale = Locale.current
    val formatter = remember(locale) { shortTimeFormatter(locale.toLanguageTag()) }
    val time = remember(record.timestampMillis, formatter, timeZone) {
        val localTime = Instant.fromEpochMilliseconds(record.timestampMillis).toLocalDateTime(timeZone).time
        formatter(localTime)
    }

    if (record.values.size <= 1) return time

    val values = record.values.joinToString(separator = stringResource(Res.string.roll_history_value_separator))
    return stringResource(Res.string.roll_history_row_details, values, time)
}
