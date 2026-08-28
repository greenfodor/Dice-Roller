package com.greenfodor.diceroller.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RollHistoryDao {
    /** Every recorded roll, newest first. */
    @Query("SELECT * FROM roll_records ORDER BY timestamp_millis DESC, id DESC")
    fun observeAll(): Flow<List<RollRecordEntity>>

    @Insert
    suspend fun insert(entity: RollRecordEntity)

    @Query("DELETE FROM roll_records")
    suspend fun clear()
}
