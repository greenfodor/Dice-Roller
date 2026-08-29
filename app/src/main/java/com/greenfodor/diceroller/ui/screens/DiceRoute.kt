package com.greenfodor.diceroller.ui.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The dice home: the die-type picker plus whichever dice screen is selected. */
@Serializable
data object DiceRoute : NavKey
