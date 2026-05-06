package com.greenfodor.diceroller.ui.screens

import androidx.annotation.StringRes
import com.greenfodor.diceroller.R

enum class DiceType(
    @StringRes val labelResId: Int
) {
    SINGLE_D4(labelResId = R.string.d4_label),
    SINGLE_D6(labelResId = R.string.d6_label),
    DOUBLE_D6(labelResId = R.string.double_d6_label),
    SINGLE_D8(labelResId = R.string.d8_label),
    SINGLE_D20(labelResId = R.string.d20_label)
}
