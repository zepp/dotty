package im.point.dotty.db

import androidx.room.TypeConverter
import im.point.dotty.model.Gender

class GenderConverter {
    @TypeConverter
    fun fromInt(value: String?): Gender? {
        return value?.let{Gender.valueOf(it)}
    }

    @TypeConverter
    fun fromGender(gender: Gender?): String? {
        return gender?.name
    }
}