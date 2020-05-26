package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "users")
data class User (@PrimaryKey
                 val id: Long) {
    var login: String? = null
    var name: String? = null
    @ColumnInfo(name = "registration_date")
    var registrationDate: Date? = null
    @ColumnInfo(name = "birth_date")
    var birthDate: Date? = null
    var gender: Gender? = null
    var about: String? = null
}