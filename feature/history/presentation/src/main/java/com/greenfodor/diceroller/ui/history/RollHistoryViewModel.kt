package com.greenfodor.diceroller.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.groupRollsByDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import javax.inject.Inject

/**
 * Exposes the persisted roll history grouped by calendar day.
 *
 * [clock] supplies the "now" the day grouping is measured against, so it can be pinned in
 * tests. Recording is owned by the dice screen and clearing by the settings screen, so this
 * ViewModel only reads.
 */
@HiltViewModel
class RollHistoryViewModel
    @Inject
    constructor(
        private val repository: RollHistoryRepository,
        private val clock: Clock
    ) : ViewModel() {
        val uiState: StateFlow<RollHistoryUiState> =
            repository.rolls
                .map { rolls ->
                    if (rolls.isEmpty()) {
                        RollHistoryUiState.Empty
                    } else {
                        RollHistoryUiState.Content(groupRollsByDay(rolls, clock.instant(), clock.zone))
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = RollHistoryUiState.Loading
                )

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
