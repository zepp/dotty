/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "posts")
data class Post(@PrimaryKey
                val id: String,
                @ColumnInfo(name = "author_id")
                val authorId: Long,
                @ColumnInfo(name = "author_login")
                var authorLogin: String) {
    var name: String? = null

    var text: String? = null
    var tags: List<String>? = null
    var timestamp: Date? = null
    var commentCount: Int? = null

    var pinned: Boolean? = null
    var private: Boolean? = null

    val nameOrLogin: String?
        get() = if (name.isNullOrEmpty()) authorLogin else name

    val alogin:String
        get() = "@$authorLogin"
}