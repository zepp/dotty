package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag_last_page_ids")
data class TagLastPageId(@PrimaryKey val tag: String,
                         @ColumnInfo(name = "page_id")
                         val pageId: Long)