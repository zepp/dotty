/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromString(value: String) =
        if (value.isNullOrEmpty()) listOf() else value.split(',')

    @TypeConverter
    fun fromList(value: List<String>) = with(StringBuilder()) {
        value.joinTo(this, ",")
        toString()
    }
}