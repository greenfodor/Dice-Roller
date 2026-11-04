package com.greenfodor.diceroller.ui.history

import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollRecord

private const val VALUE_SEPARATOR = " + "
private const val BREAKDOWN_PREFIX = "("
private const val BREAKDOWN_POSTFIX = ")"
private const val TENS_DIGITS = 2

/**
 * The wheel line for [record]: its die notation, its scored total, and — only for a roll of more
 * than one die — the individual values in brackets. A [DieLabels.D100] renders its tens die with
 * two digits, so a rolled 100 breaks down as `(00 + 0)`.
 */
internal fun recentRollUiModel(record: RollRecord): RecentRollUiModel = RecentRollUiModel(
    id = record.id,
    dieLabel = record.dieLabel,
    total = record.total.toString(),
    breakdown = record.values
        .takeIf { it.size > 1 }
        ?.mapIndexed { index, value -> record.dieLabel.formatValue(index, value) }
        ?.joinToString(
            separator = VALUE_SEPARATOR,
            prefix = BREAKDOWN_PREFIX,
            postfix = BREAKDOWN_POSTFIX
        )
)

private fun String.formatValue(index: Int, value: Int): String =
    if (this == DieLabels.D100 && index == 0) {
        value.toString().padStart(TENS_DIGITS, '0')
    } else {
        value.toString()
    }
