/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo

abstract class MetaPost {
    abstract val id: String
    abstract val authorId: Long
    abstract val commentId: String?

    var bookmarked = false
    var recommended = false
    var subscribed = false

    @ColumnInfo(name = "page_id")
    var pageId: Long? = null
}