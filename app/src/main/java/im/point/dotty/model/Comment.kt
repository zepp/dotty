package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "comments", primaryKeys = ["post_id", "id"])
data class Comment(@ColumnInfo(name = "post_id")
                   val postId: String,
                   val id: Int,
                   @ColumnInfo(name = "user_id")
                   val userId: Long) {

    @ColumnInfo(name = "parent_id")
    var parentId: Int? = 0
    var text: String? = null
    var timestamp: Date? = null
}