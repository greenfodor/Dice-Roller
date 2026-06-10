package com.greenfodor.diceroller.data

/**
 * Identifies a die (or one of two dice on a multi-die screen) that can be colored independently
 * when "single color for all dice" is off. [D6_SECONDARY] is the second die on the double-D6
 * screen; [D100_SECONDARY] is the units die on the percentile-D100 screen.
 */
enum class DieColorTarget {
    D4,
    D6,
    D6_SECONDARY,
    D8,
    D10,
    D20,
    D100,
    D100_SECONDARY
    ;

    companion object {
        /** Parse a persisted [DieColorTarget.name], returning `null` for unknown/missing values. */
        fun fromName(name: String?): DieColorTarget? = entries.firstOrNull { it.name == name }
    }
}
