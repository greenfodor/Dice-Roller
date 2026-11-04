package com.greenfodor.diceroller.data

import java.io.File

/** The per-user `~/.diceroller` directory holding the app's files, created if missing. */
internal fun appDataDirectory(): File =
    File(System.getProperty("user.home"), ".diceroller").apply { mkdirs() }
