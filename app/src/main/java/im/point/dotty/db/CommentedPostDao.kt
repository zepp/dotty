/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.CommentedPost
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentedPostDao : PostDao<CommentedPost>{
    @Query("SELECT * FROM commented_posts ORDER BY page_id ASC")
    fun getAll(): Flow<List<CommentedPost>>

    @Query("SELECT * FROM commented_posts WHERE id = :id")
    fun getPost(id: String): Flow<CommentedPost?>

    @Query("DELETE FROM commented_posts")
    fun deleteAll()
}