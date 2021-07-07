package im.point.dotty.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_datum")
data class UserData(@PrimaryKey val userId: Long,
                    val lastPageId: Long)