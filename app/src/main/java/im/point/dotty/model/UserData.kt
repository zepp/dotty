package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_datum")
data class UserData(@PrimaryKey
                    @ColumnInfo(name = "user_id")
                    val userId: Long,
                    @ColumnInfo(name = "last_page_id")
                    val lastPageId: Long)