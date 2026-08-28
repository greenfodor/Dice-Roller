package com.greenfodor.diceroller.ui.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.feature.history.presentation.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** The header text for [day]: `Today`, `Yesterday`, or the date in the viewer's locale. */
@Composable
internal fun rollHistoryDayLabel(day: RollHistoryDay): String = when (day) {
    RollHistoryDay.Today -> stringResource(R.string.roll_history_today)
    RollHistoryDay.Yesterday -> stringResource(R.string.roll_history_yesterday)
    is RollHistoryDay.Earlier -> {
        val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }
        formatter.format(day.date)
    }
}

/**
 * The secondary line of a history row: the time the roll happened, preceded by the individual
 * die values when the roll used more than one die.
 */
@Composable
internal fun rollHistoryDetails(record: RollRecord): String {
    val formatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
    val time = remember(record.timestampMillis, formatter) {
        formatter.format(
            Instant.ofEpochMilli(record.timestampMillis).atZone(ZoneId.systemDefault()).toLocalTime()
        )
    }

    if (record.values.size <= 1) return time

    val values = record.values.joinToString(separator = stringResource(R.string.roll_history_value_separator))
    return stringResource(R.string.roll_history_row_details, values, time)
}
