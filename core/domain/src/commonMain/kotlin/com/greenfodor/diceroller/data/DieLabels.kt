package com.greenfodor.diceroller.data

/**
 * The stable, non-localized notation recorded with every roll and shown in the roll history.
 *
 * These strings are persisted, so changing one orphans the rows already written under the old
 * value. A screen that rolls several dice at once has a single label covering the whole roll
 * ([DOUBLE_D6], [D100]).
 */
object DieLabels {
    const val D4 = "d4"
    const val D6 = "d6"
    const val DOUBLE_D6 = "2d6"
    const val D8 = "d8"
    const val D10 = "d10"
    const val D20 = "d20"
    const val D100 = "d100"
}
