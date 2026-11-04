package com.greenfodor.diceroller.data.history

import androidx.room.Room
import androidx.room.RoomDatabase
import com.greenfodor.diceroller.data.appDataDirectory

/** Builder for the roll history database at `~/.diceroller/roll_history.db`. */
internal fun rollHistoryDatabaseBuilder(): RoomDatabase.Builder<RollHistoryDatabase> =
    Room.databaseBuilder<RollHistoryDatabase>(
        name = appDataDirectory().resolve(ROLL_HISTORY_DATABASE_NAME).absolutePath
    )
