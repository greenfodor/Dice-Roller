package com.greenfodor.diceroller.ui.utils

import androidx.compose.runtime.compositionLocalOf
import com.greenfodor.diceroller.data.D6FaceStyle

/**
 * How the D6's faces are marked (pips vs. numbers), resolved from the persisted user setting.
 * Provided by the root composable; defaults to [D6FaceStyle.PIPS] so previews and screens used
 * outside the app shell render the classic dotted die.
 */
val LocalD6FaceStyle = compositionLocalOf { D6FaceStyle.PIPS }
