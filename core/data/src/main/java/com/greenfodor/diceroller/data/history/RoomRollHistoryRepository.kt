package com.greenfodor.diceroller.data.history

import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.RollRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** [RollHistoryRepository] backed by the [RollHistoryDatabase] Room database. */
class RoomRollHistoryRepository(
    private val dao: RollHistoryDao
) : RollHistoryRepository {
    override val rolls: Flow<List<RollRecord>> =
        dao.observeAll().map { entities -> entities.map(RollRecordEntity::toRollRecord) }

    override suspend fun record(record: RollRecord) {
        dao.insert(record.toRollRecordEntity())
    }

    override suspend fun clear() {
        dao.clear()
    }
}
