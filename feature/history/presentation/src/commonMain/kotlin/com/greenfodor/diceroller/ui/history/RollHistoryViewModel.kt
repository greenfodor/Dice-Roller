package com.greenfodor.diceroller.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.groupRollsByDay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Exposes the persisted roll history grouped by calendar day.
 *
 * [clock] supplies the "now" the day grouping is measured against and [timeZone] the zone both
 * that grouping and the row times are rendered in, so both can be pinned in tests. The grouping
 * is redone at every local midnight, so a history left open overnight moves its rolls out of
 * "Today". A read failure surfaces as [RollHistoryUiState.Error].
 *
 * Recording is owned by the dice screen and clearing by the settings screen, so this ViewModel
 * only reads.
 */
class RollHistoryViewModel(
    private val repository: RollHistoryRepository,
    private val clock: Clock,
    val timeZone: TimeZone
) : ViewModel() {
    val uiState: StateFlow<RollHistoryUiState> =
        combine(repository.rolls, dayBoundaries()) { rolls, now ->
            if (rolls.isEmpty()) {
                RollHistoryUiState.Empty
            } else {
                RollHistoryUiState.Content(groupRollsByDay(rolls, now, timeZone))
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
            val now = clock.now()
            emit(now)
            val nextMidnight = now.toLocalDateTime(timeZone).date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
            delay((nextMidnight - now).inWholeMilliseconds.coerceAtLeast(1L))
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
