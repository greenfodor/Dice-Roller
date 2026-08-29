package com.greenfodor.diceroller.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.greenfodor.diceroller.R

enum class DiceType(
    @field:StringRes val labelResId: Int,
    @field:DrawableRes val iconResId: Int
) {
    SINGLE_D4(labelResId = R.string.d4_label, iconResId = R.drawable.ic_die_d4),
    SINGLE_D6(labelResId = R.string.d6_label, iconResId = R.drawable.ic_die_d6),
    DOUBLE_D6(labelResId = R.string.double_d6_label, iconResId = R.drawable.ic_die_double_d6),
    SINGLE_D8(labelResId = R.string.d8_label, iconResId = R.drawable.ic_die_d8),
    SINGLE_D10(labelResId = R.string.d10_label, iconResId = R.drawable.ic_die_d10),
    SINGLE_D20(labelResId = R.string.d20_label, iconResId = R.drawable.ic_die_d20),
    PERCENTILE_D100(labelResId = R.string.d100_label, iconResId = R.drawable.ic_die_d100)
}
