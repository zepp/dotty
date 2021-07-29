/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import im.point.dotty.model.AllPost
import im.point.dotty.model.CompleteAllPost
import kotlinx.coroutines.flow.Flow

@Dao
interface AllPostDao : CommonDao<AllPost> {
    @Transaction
    @Query("SELECT * FROM all_posts WHERE id = :id")
    fun getItem(id: String): CompleteAllPost?

    @Transaction
    @Query("SELECT * FROM all_posts ORDER BY page_id ASC")
    fun getAll(): List<CompleteAllPost>

    @Transaction
    @Query("SELECT * FROM all_posts ORDER BY page_id ASC")
    fun getAllFlow(): Flow<List<CompleteAllPost>>

    @Transaction
    @Query("SELECT * FROM all_posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<CompleteAllPost?>

    @Query("SELECT * FROM all_posts WHERE id = :id")
    fun getMetaPostFlow(id: String): Flow<AllPost?>

    @Query("DELETE FROM all_posts WHERE id = :id")
    fun removeItem(id: String)
}