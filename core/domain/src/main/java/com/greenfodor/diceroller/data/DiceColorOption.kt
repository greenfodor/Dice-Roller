package com.greenfodor.diceroller.data

/**
 * A user-selectable dice color from a predefined palette. Each option maps to a light and a dark
 * shade in `:core:designsystem` (only the current-theme variant is shown to the user).
 *
 * To add a new color: add an entry here, then add the matching light/dark branch to
 * `DiceColorOption.toColor` in `:core:designsystem`.
 */
enum class DiceColorOption {
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    TEAL,
    BLUE,
    PURPLE,
    PINK,
    BROWN,
    GRAY
    ;

    companion object {
        /** Parse a persisted [DiceColorOption.name], falling back to [RED] for unknown/missing values. */
        fun fromName(name: String?): DiceColorOption = entries.firstOrNull { it.name == name } ?: RED
    }
}
