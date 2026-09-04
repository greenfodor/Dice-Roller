package com.greenfodor.diceroller.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenfodor.diceroller.data.RollHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Exposes the newest [RECENT_ROLLS_COUNT] rolls as the lines the recent rolls wheel renders.
 *
 * The repository reports its rolls newest first; [recentRolls] hands them over in render order
 * instead — oldest first, newest last — so the newest sits at the bottom of the wheel. A read
 * failure surfaces as an empty wheel.
 */
@HiltViewModel
class RecentRollsViewModel
    @Inject
    constructor(
        repository: RollHistoryRepository
    ) : ViewModel() {
        val recentRolls: StateFlow<List<RecentRollUiModel>> =
            repository.rolls
                .map { rolls ->
                    rolls.take(RECENT_ROLLS_COUNT)
                        .reversed()
                        .map(::recentRollUiModel)
                }
                .catch { emit(emptyList()) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = emptyList()
                )

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
