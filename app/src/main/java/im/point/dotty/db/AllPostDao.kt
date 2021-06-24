/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.AllPost
import kotlinx.coroutines.flow.Flow

@Dao
interface AllPostDao : CommonDao<AllPost, String> {
    @Query("SELECT * FROM all_posts WHERE id = :id")
    override fun getItem(id: String): AllPost?

    @Query("SELECT * FROM all_posts ORDER BY page_id ASC")
    fun getAll(): List<AllPost>

    @Query("SELECT * FROM all_posts ORDER BY page_id ASC")
    fun getAllFlow(): Flow<List<AllPost>>

    @Query("SELECT * FROM all_posts WHERE id = :id")
    fun getItemFlow(id: String): Flow<AllPost?>
}