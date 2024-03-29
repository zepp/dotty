/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "tagged_posts", primaryKeys = ["id", "tag"])
data class TaggedPost(override val id: String,
                      @ColumnInfo(name = "author_id")
                      override val authorId: Long,
                      @ColumnInfo(name = "comment_id")
                      override val commentId: String? = id,
                      @ColumnInfo(index = true)
                      val tag: String) : MetaPost()