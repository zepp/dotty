/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_posts")
data class UserPost(@PrimaryKey
                    override val id: String,
                    @ColumnInfo(name = "author_id")
                    override val authorId: Long,
                    @ColumnInfo(name = "user_id")
                    val userId: Long) : Post()