package com.greenfodor.diceroller.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.ui.theme.spacing
import java.time.ZoneId

/** One recorded roll: the die notation and time on the left, the scored total on the right. */
@Composable
internal fun RollHistoryRow(
    record: RollRecord,
    zoneId: ZoneId,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.dieLabel,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = rollHistoryDetails(record = record, zoneId = zoneId),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = record.total.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
