package com.greenfodor.diceroller.ui.history

import com.greenfodor.diceroller.data.DieLabels
import com.greenfodor.diceroller.data.RollRecord
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentRollsFormattingTest {
    @Test
    fun `a single die roll renders its label and total with no breakdown`() {
        assertEquals(
            RecentRollUiModel(id = 1, dieLabel = "d6", total = "6"),
            recentRollUiModel(roll(DieLabels.D6, values = listOf(6), total = 6))
        )
    }

    @Test
    fun `a multi die roll breaks its total down into the individual values`() {
        assertEquals(
            RecentRollUiModel(id = 1, dieLabel = "2d6", total = "7", breakdown = "(3 + 4)"),
            recentRollUiModel(roll(DieLabels.DOUBLE_D6, values = listOf(3, 4), total = 7))
        )
    }

    @Test
    fun `a percentile roll of 100 renders its tens die as two digits`() {
        assertEquals(
            RecentRollUiModel(id = 1, dieLabel = "d100", total = "100", breakdown = "(00 + 0)"),
            recentRollUiModel(roll(DieLabels.D100, values = listOf(0, 0), total = 100))
        )
    }

    @Test
    fun `a percentile roll renders its tens die unpadded once it has two digits`() {
        assertEquals(
            RecentRollUiModel(id = 1, dieLabel = "d100", total = "42", breakdown = "(40 + 2)"),
            recentRollUiModel(roll(DieLabels.D100, values = listOf(40, 2), total = 42))
        )
    }

    private fun roll(dieLabel: String, values: List<Int>, total: Int): RollRecord =
        RollRecord(
            id = 1,
            dieLabel = dieLabel,
            values = values,
            total = total,
            timestampMillis = TIMESTAMP_MILLIS
        )

    private companion object {
        const val TIMESTAMP_MILLIS = 1_787_000_000_000L
    }
}
