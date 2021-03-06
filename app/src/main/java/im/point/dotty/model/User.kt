/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "users")
data class User(@PrimaryKey
                val id: Long,
                val login: String,
                var name: String? = null,
                @ColumnInfo(name = "registration_date")
                var registrationDate: Date? = null,

                @ColumnInfo(name = "birth_date")
                var birthDate: Date? = null,
                var gender: Gender? = null,
                var about: String? = null,

                var subscribed: Boolean? = null,
                var blocked: Boolean? = null,
                var recSubscribed: Boolean? = null) {

    val formattedLogin: String
        get() = "@$login"
}