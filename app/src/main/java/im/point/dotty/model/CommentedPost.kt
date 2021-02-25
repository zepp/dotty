/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "commented_posts")
data class CommentedPost(@PrimaryKey
                         val id: String,
                         @ColumnInfo(name = "user_id")
                         val userId: Long) : Post() {

    @Ignore
    override val postId: String = id
}