package com.greenfodor.diceroller.ui.screens

import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.d100_label
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.d10_label
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.d20_label
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.d4_label
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.d6_label
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.d8_label
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.double_d6_label
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.ic_die_d10
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.ic_die_d100
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.ic_die_d20
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.ic_die_d4
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.ic_die_d6
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.ic_die_d8
import com.greenfodor.diceroller.feature.dice.presentation.generated.resources.ic_die_double_d6
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class DiceType(
    val label: StringResource,
    val icon: DrawableResource
) {
    SINGLE_D4(label = Res.string.d4_label, icon = Res.drawable.ic_die_d4),
    SINGLE_D6(label = Res.string.d6_label, icon = Res.drawable.ic_die_d6),
    DOUBLE_D6(label = Res.string.double_d6_label, icon = Res.drawable.ic_die_double_d6),
    SINGLE_D8(label = Res.string.d8_label, icon = Res.drawable.ic_die_d8),
    SINGLE_D10(label = Res.string.d10_label, icon = Res.drawable.ic_die_d10),
    SINGLE_D20(label = Res.string.d20_label, icon = Res.drawable.ic_die_d20),
    PERCENTILE_D100(label = Res.string.d100_label, icon = Res.drawable.ic_die_d100)
    ;

    companion object {
        /** Parse a persisted [DiceType.name], falling back to [SINGLE_D6] for unknown/missing values. */
        fun fromName(name: String?): DiceType = entries.firstOrNull { it.name == name } ?: SINGLE_D6
    }
}
