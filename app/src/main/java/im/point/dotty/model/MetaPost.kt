package im.point.dotty.model

import androidx.room.ColumnInfo

abstract class MetaPost() {
    abstract val id: String
    abstract val authorId: Long

    var bookmarked = false
    var recommended = false
    var subscribed = false

    @ColumnInfo(name = "page_id")
    var pageId: Long? = null
}