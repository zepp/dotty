/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import im.point.dotty.model.CompleteRecentPost
import im.point.dotty.model.RecentPost
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPostDao : CommonDao<RecentPost> {
    @Transaction
    @Query("SELECT * FROM recent_posts WHERE id = :id")
    fun getItem(id: String): CompleteRecentPost?

    @Transaction
    @Query("SELECT * FROM recent_posts ORDER BY page_id DESC")
    fun getAll(): List<CompleteRecentPost>

    @Transaction
    @Query("SELECT * FROM recent_posts ORDER BY page_id DESC")
    fun getAllFlow(): Flow<List<CompleteRecentPost>>

    @Transaction
    @Query("SELECT * FROM recent_posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<CompleteRecentPost?>

    @Query("SELECT * FROM recent_posts WHERE id = :id")
    fun getMetaPostFlow(id: String): Flow<RecentPost?>
}