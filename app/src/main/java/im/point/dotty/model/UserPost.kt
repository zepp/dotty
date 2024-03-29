/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "user_posts", primaryKeys = ["id", "user_id"])
data class UserPost(override val id: String,
                    @ColumnInfo(name = "author_id")
                    override val authorId: Long,
                    @ColumnInfo(name = "comment_id")
                    override val commentId: String?,
                    @ColumnInfo(name = "user_id", index = true)
                    val userId: Long) : MetaPost()