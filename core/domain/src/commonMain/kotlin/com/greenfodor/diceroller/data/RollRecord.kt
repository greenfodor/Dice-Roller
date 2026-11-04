package com.greenfodor.diceroller.data

/**
 * One completed roll.
 *
 * A screen that rolls several dice at once (double d6, percentile d100) records a **single**
 * [RollRecord]: [values] holds every die's face value in the order they are shown and [total]
 * holds the screen's scored outcome, which is not always the plain sum (d100 scores 00 + 0
 * as 100).
 *
 * @param id Row identifier; `0` for a record that has not been persisted yet.
 * @param dieLabel Stable, non-localized notation for the roll — one of the [DieLabels] constants.
 * @param values Each die's face value, in display order.
 * @param total The scored outcome shown to the user.
 * @param timestampMillis When the roll happened, in epoch milliseconds.
 */
data class RollRecord(
    val id: Long = 0L,
    val dieLabel: String,
    val values: List<Int>,
    val total: Int,
    val timestampMillis: Long
)
