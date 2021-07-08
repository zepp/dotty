/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao : CommonDao<Post> {
    @Query("SELECT * FROM posts WHERE id = :id")
    fun getItem(id: String): Post?

    @Query("SELECT * FROM posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<Post?>

    @Query("SELECT * FROM posts")
    fun getAllFlow(): Flow<List<Post>>
}