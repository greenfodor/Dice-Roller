package com.greenfodor.diceroller.data.history

import androidx.room.TypeConverter

/** Stores a die-value list as a comma-separated string column. */
class IntListConverter {
    @TypeConverter
    fun fromIntList(values: List<Int>): String = values.joinToString(separator = SEPARATOR)

    @TypeConverter
    fun toIntList(stored: String): List<Int> =
        if (stored.isEmpty()) emptyList() else stored.split(SEPARATOR).map(String::toInt)

    private companion object {
        const val SEPARATOR = ","
    }
}
