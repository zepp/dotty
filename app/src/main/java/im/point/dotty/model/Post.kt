/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import java.util.*

abstract class Post {
    abstract val id: String
    abstract val authorId: Long
    abstract var authorLogin: String

    var name: String? = null

    @ColumnInfo(name = "page_id")
    var pageId: Long? = null
    var text: String? = null
    var tags: List<String>? = null
    var timestamp: Date? = null
    var commentCount: Int? = null

    var bookmarked: Boolean? = null
    var recommended: Boolean? = null
    var subscribed: Boolean? = null
    var pinned: Boolean? = null
    var private: Boolean? = null

    val nameOrLogin: String?
        get() = if (name.isNullOrEmpty()) authorLogin else name

    val alogin:String
        get() = "@$authorLogin"
}