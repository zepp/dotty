package im.point.dotty.model

import androidx.room.TypeConverter

class GenderConverter {
    @TypeConverter
    fun fromInt(value: String?): Gender {
        return Gender.valueOf(value!!)
    }

    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }
}