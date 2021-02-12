package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "recent_posts")
data class RecentPost(@PrimaryKey
                 val id: String,
                 @ColumnInfo(name = "user_id")
                 val userId: Long) : Post() {
    @Ignore
    override val postId: String = id
}