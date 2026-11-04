package com.greenfodor.diceroller.data

/**
 * The result of one completed roll, before it is persisted as a [RollRecord].
 *
 * A screen that rolls several dice at once produces a single outcome: [values] holds every
 * die's face value in display order and [total] the screen's scored result, which is not
 * always the plain sum — percentile d100 scores 00 + 0 as 100.
 *
 * @param dieLabel One of the [DieLabels] constants.
 * @param startedAtMillis When the roll was started, in epoch milliseconds. Becomes the
 *   record's [RollRecord.timestampMillis].
 */
data class RollOutcome(
    val dieLabel: String,
    val values: List<Int>,
    val total: Int,
    val startedAtMillis: Long
)
