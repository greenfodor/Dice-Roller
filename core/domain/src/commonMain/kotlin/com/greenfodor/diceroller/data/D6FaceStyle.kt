package com.greenfodor.diceroller.data

/**
 * How a D6's faces are marked. [PIPS] is the default (the classic dot layout); [NUMBERS] draws
 * the digit 1–6 instead. Only the D6 supports pips — the other dice are always numbered.
 */
enum class D6FaceStyle {
    PIPS,
    NUMBERS
    ;

    companion object {
        /** Parse a persisted [D6FaceStyle.name], falling back to [PIPS] for unknown/missing values. */
        fun fromName(name: String?): D6FaceStyle = entries.firstOrNull { it.name == name } ?: PIPS
    }
}
