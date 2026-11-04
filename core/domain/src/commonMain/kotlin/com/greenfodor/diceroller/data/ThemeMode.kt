package com.greenfodor.diceroller.data

/**
 * User-selectable app theme. [FOLLOW_SYSTEM] is the default — the app tracks the device's
 * light/dark setting until the user explicitly picks [LIGHT] or [DARK].
 */
enum class ThemeMode {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK
    ;

    companion object {
        /** Parse a persisted [ThemeMode.name], falling back to [FOLLOW_SYSTEM] for unknown/missing values. */
        fun fromName(name: String?): ThemeMode = entries.firstOrNull { it.name == name } ?: FOLLOW_SYSTEM
    }
}
