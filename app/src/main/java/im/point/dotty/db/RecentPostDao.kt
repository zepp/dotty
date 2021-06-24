/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.RecentPost
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPostDao : CommonDao<RecentPost, String> {
    @Query("SELECT * FROM recent_posts WHERE id = :id")
    override fun getItem(id: String): RecentPost?

    @Query("SELECT * FROM recent_posts ORDER BY page_id DESC")
    fun getAll(): List<RecentPost>

    @Query("SELECT * FROM recent_posts ORDER BY page_id DESC")
    fun getAllFlow(): Flow<List<RecentPost>>

    @Query("SELECT * FROM recent_posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<RecentPost?>
}