package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_files")
data class PostFile(@PrimaryKey
                    val id: String,
                    @ColumnInfo(name = "post_id", index = true)
                    val postId: String,
                    val url: String)