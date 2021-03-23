/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.RecentPost
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPostDao : PostDao<RecentPost> {
    @Query("SELECT * FROM recent_posts ORDER BY page_id DESC")
    fun getAll(): Flow<List<RecentPost>>

    @Query("SELECT * FROM recent_posts WHERE id = :id")
    fun getPost(id: String): Flow<RecentPost?>

    @Query("DELETE FROM recent_posts")
    fun deleteAll()
}