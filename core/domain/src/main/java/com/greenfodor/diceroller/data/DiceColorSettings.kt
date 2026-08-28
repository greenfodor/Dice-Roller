package com.greenfodor.diceroller.data

/**
 * The user's dice-color configuration.
 *
 * When [useSingleColor] is true, every die uses [singleColor]. Otherwise each [DieColorTarget]
 * is colored independently from [perDie], falling back to [DEFAULT_PER_DIE]. Defaults give the
 * dice distinct colors out of the box; retune the whole scheme by editing [DEFAULT_PER_DIE].
 */
data class DiceColorSettings(
    val useSingleColor: Boolean = false,
    val singleColor: DiceColorOption = DiceColorOption.RED,
    val perDie: Map<DieColorTarget, DiceColorOption> = DEFAULT_PER_DIE
) {
    /** The color option that should be applied to [target], honoring the single-color toggle. */
    fun optionFor(target: DieColorTarget): DiceColorOption =
        if (useSingleColor) {
            singleColor
        } else {
            perDie[target] ?: DEFAULT_PER_DIE.getValue(target)
        }

    companion object {
        /** Per-die defaults — distinct colors per die. Edit here to change the default scheme. */
        val DEFAULT_PER_DIE: Map<DieColorTarget, DiceColorOption> = mapOf(
            DieColorTarget.D4 to DiceColorOption.YELLOW,
            DieColorTarget.D6 to DiceColorOption.RED,
            DieColorTarget.D6_SECONDARY to DiceColorOption.BLUE,
            DieColorTarget.D8 to DiceColorOption.GREEN,
            DieColorTarget.D10 to DiceColorOption.ORANGE,
            DieColorTarget.D20 to DiceColorOption.PURPLE,
            DieColorTarget.D100 to DiceColorOption.TEAL,
            DieColorTarget.D100_SECONDARY to DiceColorOption.PINK
        )

        /** Default single color used when "single color for all dice" is enabled. */
        val DEFAULT_SINGLE_COLOR: DiceColorOption = DiceColorOption.RED
    }
}
