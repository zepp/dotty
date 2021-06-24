/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.UserPost
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPostDao : CommonDao<UserPost, String> {
    @Query("SELECT * FROM user_posts WHERE id = :id")
    override fun getItem(id: String): UserPost?

    @Query("SELECT * FROM user_posts WHERE user_id = :userId ORDER BY page_id DESC")
    fun getUserPosts(userId: Long): Flow<List<UserPost>>

    @Query("SELECT * FROM user_posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<UserPost?>
}