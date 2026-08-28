package com.greenfodor.diceroller.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.data.RollRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

/**
 * Owns persisting the rolls made on the dice screens.
 *
 * The dice themselves stay in composition — [com.greenfodor.diceroller.ui.dice.DieState] drives
 * `animateFloatAsState` targets directly — so this ViewModel is reached only once a roll has
 * finished animating.
 */
@HiltViewModel
class DiceViewModel
    @Inject
    constructor(
        private val repository: RollHistoryRepository,
        private val clock: Clock
    ) : ViewModel() {
        /** Timestamps [outcome] and appends it to the roll history as one record. */
        fun onRollSettled(outcome: RollOutcome) {
            viewModelScope.launch {
                repository.record(
                    RollRecord(
                        dieLabel = outcome.dieLabel,
                        values = outcome.values,
                        total = outcome.total,
                        timestampMillis = clock.millis()
                    )
                )
            }
        }
    }
