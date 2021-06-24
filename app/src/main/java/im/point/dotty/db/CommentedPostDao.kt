/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.CommentedPost
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentedPostDao : CommonDao<CommentedPost, String> {
    @Query("SELECT * FROM commented_posts WHERE id = :id")
    override fun getItem(id: String): CommentedPost?

    @Query("SELECT * FROM commented_posts ORDER BY page_id ASC")
    fun getAllFlow(): Flow<List<CommentedPost>>

    @Query("SELECT * FROM commented_posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<CommentedPost?>
}