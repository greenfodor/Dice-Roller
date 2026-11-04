package com.greenfodor.diceroller.ui.screens

import androidx.lifecycle.ViewModel
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.data.RollRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Owns persisting the rolls made on the dice screens.
 *
 * The dice themselves stay in composition — [com.greenfodor.diceroller.ui.dice.DieState] drives
 * `animateFloatAsState` targets directly — so this ViewModel is reached only once a roll has
 * finished animating.
 *
 * Records run on [applicationScope], so a write already in flight completes after this ViewModel
 * is cleared.
 */
class DiceViewModel(
    private val repository: RollHistoryRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {
    /** Appends [outcome] to the roll history as one record, timestamped when the roll started. */
    fun onRollSettled(outcome: RollOutcome) {
        applicationScope.launch {
            repository.record(
                RollRecord(
                    dieLabel = outcome.dieLabel,
                    values = outcome.values,
                    total = outcome.total,
                    timestampMillis = outcome.startedAtMillis
                )
            )
        }
    }
}
