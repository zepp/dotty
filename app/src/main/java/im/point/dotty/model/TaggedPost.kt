package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "tagged_posts", primaryKeys = ["id", "tag"])
data class TaggedPost(override val id: String,
                      @ColumnInfo(name = "author_id")
                      override val authorId: Long,
                      @ColumnInfo(index = true)
                      val tag: String) : MetaPost()