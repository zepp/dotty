/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import im.point.dotty.model.CompleteUserPost
import im.point.dotty.model.UserPost
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPostDao : CommonDao<UserPost> {
    @Transaction
    @Query("SELECT * FROM user_posts WHERE user_id = :userId ORDER BY page_id DESC")
    fun getUserPosts(userId: Long): List<CompleteUserPost>

    @Transaction
    @Query("SELECT * FROM user_posts WHERE user_id = :userId ORDER BY page_id DESC")
    fun getUserPostsFlow(userId: Long): Flow<List<CompleteUserPost>>

    @Transaction
    @Query("SELECT * FROM user_posts WHERE id = :id AND user_id = :userId")
    fun getItemFlow(id: String, userId: Long): Flow<CompleteUserPost?>

    @Query("SELECT * FROM user_posts WHERE id = :id AND user_id = :userId")
    fun getMetaPostFlow(id: String, userId: Long): Flow<UserPost?>

    @Query("DELETE FROM user_posts WHERE id = :id")
    fun removeItems(id: String)

    @Query("DELETE FROM user_posts WHERE id = :id AND user_id = :userId")
    fun removeItem(id: String, userId: Long)
}