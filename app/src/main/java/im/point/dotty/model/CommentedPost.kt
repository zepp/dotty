/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commented_posts")
data class CommentedPost(@PrimaryKey
                         override val id: String,
                         @ColumnInfo(name = "author_id")
                         override val authorId: Long,
                         @ColumnInfo(name = "comment_id")
                         override val commentId: String?) : MetaPost()