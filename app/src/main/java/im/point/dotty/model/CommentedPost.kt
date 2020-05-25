package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commented_posts")
data class CommentedPost(@PrimaryKey
                    val id: String,
                    @ColumnInfo(name = "user_id")
                    val userId: Long) : Post()