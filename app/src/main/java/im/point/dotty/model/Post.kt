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
                var authorLogin: String,
                var authorName: String = "",

                var text: String = "",
                var tags: List<String> = listOf(),
                var timestamp: Date? = null,
                var commentCount: Int = 0,

                var isPinned: Boolean = false,
                var isPrivate: Boolean = false) {

    val formattedLogin: String
        get() = "@$authorLogin"

    val formattedId: String
        get() = "#$id"
}