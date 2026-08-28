package com.greenfodor.diceroller.data.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RollRecordEntity::class], version = 1, exportSchema = false)
@TypeConverters(IntListConverter::class)
abstract class RollHistoryDatabase : RoomDatabase() {
    abstract fun rollHistoryDao(): RollHistoryDao

    companion object {
        private const val DATABASE_NAME = "roll_history.db"

        fun create(context: Context): RollHistoryDatabase =
            Room.databaseBuilder(context, RollHistoryDatabase::class.java, DATABASE_NAME).build()
    }
}
