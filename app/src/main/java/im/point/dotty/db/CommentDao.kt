/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao : CommonDao<Comment> {
    @Query("SELECT * FROM comments WHERE post_id = :id ")
    fun getItem(id: String): Comment?

    @Query("SELECT * FROM comments WHERE id = :id")
    fun geItemFlow(id: String): Flow<Comment?>

    @Query("SELECT * FROM comments WHERE post_id = :id")
    fun getPostComments(id: String): List<Comment>

    @Query("SELECT * FROM comments WHERE post_id = :id")
    fun getPostCommentsFlow(id: String): Flow<List<Comment>>
}