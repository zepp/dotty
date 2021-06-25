/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "comments")
data class Comment(@PrimaryKey
                   val id: String,
                   @ColumnInfo(name = "post_id")
                   val postId: String,
                   @ColumnInfo(name = "user_id")
                   val userId: Long,
                   @ColumnInfo(name = "parent_id")
                   var replyTo: Int? = 0,
                   var text: String? = null,
                   var timestamp: Date? = null,
                   var login: String? = null,
                   var name: String? = null) {

    @Ignore
    val number = id.split('/').last().toInt()

    val nameOrLogin: String?
        get() = if (name.isNullOrEmpty()) login else name
}