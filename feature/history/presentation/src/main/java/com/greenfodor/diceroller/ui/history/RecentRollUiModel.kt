package com.greenfodor.diceroller.ui.history

/** How many of the newest rolls the recent rolls wheel holds. */
const val RECENT_ROLLS_COUNT = 3

/**
 * One line of the recent rolls wheel, split into the parts the wheel styles separately.
 *
 * @param id The identifier of the record the line was rendered from.
 * @param dieLabel The die notation the roll was made with.
 * @param total The scored outcome.
 * @param breakdown The individual die values in brackets, or `null` for a single-die roll.
 */
data class RecentRollUiModel(
    val id: Long,
    val dieLabel: String,
    val total: String,
    val breakdown: String? = null
)
