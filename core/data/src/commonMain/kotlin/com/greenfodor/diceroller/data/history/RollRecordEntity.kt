package com.greenfodor.diceroller.data.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greenfodor.diceroller.data.RollRecord

/** Room row for one [RollRecord]. */
@Entity(tableName = "roll_records")
data class RollRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "die_label") val dieLabel: String,
    @ColumnInfo(name = "roll_values") val values: List<Int>,
    @ColumnInfo(name = "total") val total: Int,
    @ColumnInfo(name = "timestamp_millis") val timestampMillis: Long
)

fun RollRecordEntity.toRollRecord(): RollRecord = RollRecord(
    id = id,
    dieLabel = dieLabel,
    values = values,
    total = total,
    timestampMillis = timestampMillis
)

fun RollRecord.toRollRecordEntity(): RollRecordEntity = RollRecordEntity(
    id = id,
    dieLabel = dieLabel,
    values = values,
    total = total,
    timestampMillis = timestampMillis
)
