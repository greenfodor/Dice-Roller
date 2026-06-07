package com.greenfodor.diceroller.ui.dice.d100

import com.greenfodor.diceroller.ui.dice.DieState

/**
 * Combined percentile result for a [tens, units] pair of [DieState]s.
 * A summed value of 0 (00 + 0) reads as 100, per the standard convention.
 *
 * Lives in `:app` (not `:core:domain`) because it aggregates over the Compose-bound
 * [DieState]; the pure mapping it delegates to is [percentileValue] in `:core:domain`.
 */
fun percentileTotal(dieStates: List<DieState>): Int =
    percentileValue(dieStates.sumOf { it.currentFace.value })
