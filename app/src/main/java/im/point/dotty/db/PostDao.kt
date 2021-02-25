/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import im.point.dotty.model.Post

interface PostDao<in T : Post> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(post: T)
}