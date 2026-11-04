package com.greenfodor.diceroller.data.history

import androidx.room.Room
import androidx.room.RoomDatabase
import com.greenfodor.diceroller.data.documentDirectory

/** Builder for the roll history database in the app's document directory. */
internal fun rollHistoryDatabaseBuilder(): RoomDatabase.Builder<RollHistoryDatabase> =
    Room.databaseBuilder<RollHistoryDatabase>(
        name = "${documentDirectory()}/$ROLL_HISTORY_DATABASE_NAME"
    )
