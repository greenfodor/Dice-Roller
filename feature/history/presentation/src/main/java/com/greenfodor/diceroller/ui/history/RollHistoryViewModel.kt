package com.greenfodor.diceroller.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.groupRollsByDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * Exposes the persisted roll history grouped by calendar day.
 *
 * [clock] supplies the "now" the day grouping is measured against and [zoneId] the zone both
 * that grouping and the row times are rendered in, so both can be pinned in tests. The grouping
 * is redone at every local midnight, so a history left open overnight moves its rolls out of
 * "Today". A read failure surfaces as [RollHistoryUiState.Error].
 *
 * Recording is owned by the dice screen and clearing by the settings screen, so this ViewModel
 * only reads.
 */
@HiltViewModel
class RollHistoryViewModel
    @Inject
    constructor(
        private val repository: RollHistoryRepository,
        private val clock: Clock,
        val zoneId: ZoneId
    ) : ViewModel() {
        val uiState: StateFlow<RollHistoryUiState> =
            combine(repository.rolls, dayBoundaries()) { rolls, now ->
                if (rolls.isEmpty()) {
                    RollHistoryUiState.Empty
                } else {
                    RollHistoryUiState.Content(groupRollsByDay(rolls, now, zoneId))
                }
            }
                .catch { emit(RollHistoryUiState.Error) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = RollHistoryUiState.Loading
                )

        /** Emits the current instant, then again at every following local midnight. */
        private fun dayBoundaries(): Flow<Instant> = flow {
            while (true) {
                val now = clock.instant()
                emit(now)
                val nextMidnight = now.atZone(zoneId).toLocalDate().plusDays(1).atStartOfDay(zoneId).toInstant()
                delay(Duration.between(now, nextMidnight).toMillis().coerceAtLeast(1L))
            }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
