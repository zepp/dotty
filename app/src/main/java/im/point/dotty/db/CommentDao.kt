/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao : CommonDao<Comment, String> {
    // TO BE FIXED
    @Query("SELECT * FROM comments WHERE post_id = :id ")
    override fun getItem(id: String): Comment?

    @Query("SELECT * FROM comments WHERE post_id = :id ORDER BY id")
    fun getPostComments(id: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE post_id = :postId AND id = :id")
    fun geItemFlow(postId: String, id: Long): Flow<Comment?>
}