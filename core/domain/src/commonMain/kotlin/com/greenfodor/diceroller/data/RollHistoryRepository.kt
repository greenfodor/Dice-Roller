package com.greenfodor.diceroller.data

import kotlinx.coroutines.flow.Flow

/**
 * Persisted roll history. Implemented by `:core:data`'s Room-backed repository; kept as an
 * interface so the presentation layer can be unit-tested with a fake.
 */
interface RollHistoryRepository {
    /** Every recorded roll, newest first. */
    val rolls: Flow<List<RollRecord>>

    /** Append [record] to the history. */
    suspend fun record(record: RollRecord)

    /** Remove every recorded roll. */
    suspend fun clear()
}
