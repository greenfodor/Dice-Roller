package com.greenfodor.diceroller.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.ui.theme.spacing

/** Sticky section header naming the calendar day the rolls beneath it belong to. */
@Composable
internal fun RollHistoryDayHeader(
    day: RollHistoryDay,
    modifier: Modifier = Modifier
) {
    Text(
        text = rollHistoryDayLabel(day),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(vertical = MaterialTheme.spacing.small)
    )
}
