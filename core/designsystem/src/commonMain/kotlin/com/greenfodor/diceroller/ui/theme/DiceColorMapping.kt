package com.greenfodor.diceroller.ui.theme

import androidx.compose.ui.graphics.Color
import com.greenfodor.diceroller.data.DiceColorOption

/**
 * The (lightTheme, darkTheme) token pair for each palette [DiceColorOption]. The deeper/saturated
 * shade is used in the light theme and the brighter/paler shade in the dark theme, so each color
 * keeps strong contrast against its background. This is the single place that pairs a palette
 * option with its tokens — adding a new color means adding an entry to [DiceColorOption] and a
 * row here.
 */
private val paletteShades: Map<DiceColorOption, Pair<Color, Color>> = mapOf(
    DiceColorOption.RED to (DiceRedDark to DiceRed),
    DiceColorOption.ORANGE to (DiceOrangeDark to DiceOrange),
    DiceColorOption.YELLOW to (DiceYellowDark to DiceYellow),
    DiceColorOption.GREEN to (DiceGreenDark to DiceGreen),
    DiceColorOption.TEAL to (DiceTealDark to DiceTeal),
    DiceColorOption.BLUE to (DiceBlueDark to DiceBlue),
    DiceColorOption.PURPLE to (DicePurpleDark to DicePurple),
    DiceColorOption.PINK to (DicePinkDark to DicePink),
    DiceColorOption.BROWN to (DiceBrownDark to DiceBrown),
    DiceColorOption.GRAY to (DiceGrayDark to DiceGray)
)

/**
 * Resolves a palette [DiceColorOption] to its concrete [Color], picking the light- or dark-theme
 * shade for the current theme.
 */
fun DiceColorOption.toColor(isDark: Boolean): Color {
    val (lightTheme, darkTheme) = paletteShades.getValue(this)
    return if (isDark) darkTheme else lightTheme
}
