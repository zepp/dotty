/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(',')
    }

    @TypeConverter
    fun fromList(value: List<String>?) : String? {
        var buf = StringBuilder()
        value?.joinTo(buf, ",")
        return if (buf.isEmpty()) null else buf.toString()
    }
}