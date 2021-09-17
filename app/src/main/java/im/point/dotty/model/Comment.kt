/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "post_id")
    val postId: String,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    val number: Int,
    var login: String,
    @ColumnInfo(name = "reply_to")
    var replyTo: Int? = 0,
    var text: String? = null,
    var timestamp: Date? = null,
    var name: String? = null
) {

    val formattedLogin: String
        get() = "@$login"

    val formattedId: String
        get() = "#$id"
}