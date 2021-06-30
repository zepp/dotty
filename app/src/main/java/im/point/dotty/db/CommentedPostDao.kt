/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.CompleteCommentedPost
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentedPostDao : CommonDao<CommentedPost> {
    @Transaction
    @Query("SELECT * FROM commented_posts WHERE id = :id")
    fun getItem(id: String): CompleteCommentedPost?

    @Transaction
    @Query("SELECT * FROM commented_posts ORDER BY page_id ASC")
    fun getAll(): List<CompleteCommentedPost>

    @Transaction
    @Query("SELECT * FROM commented_posts ORDER BY page_id ASC")
    fun getAllFlow(): Flow<List<CompleteCommentedPost>>

    @Transaction
    @Query("SELECT * FROM commented_posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<CompleteCommentedPost?>

    @Query("SELECT * FROM commented_posts WHERE id = :id")
    fun getMetaPostFlow(id: String): Flow<CommentedPost?>
}