package com.greenfodor.diceroller.data.history

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/** Builder for the roll history database at `getDatabasePath("roll_history.db")`. */
internal fun rollHistoryDatabaseBuilder(context: Context): RoomDatabase.Builder<RollHistoryDatabase> {
    val appContext = context.applicationContext
    return Room.databaseBuilder<RollHistoryDatabase>(
        context = appContext,
        name = appContext.getDatabasePath(ROLL_HISTORY_DATABASE_NAME).absolutePath
    )
}
