/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_tags")
data class UserTag(@PrimaryKey
                   @ColumnInfo(name = "user_id")
                   val userId: Long,
                   val tag: String,
                   @ColumnInfo(name = "post_count")
                   val postCount: Int)