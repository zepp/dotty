/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.AllPost
import kotlinx.coroutines.flow.Flow

@Dao
interface AllPostDao : PostDao<AllPost>{
    @Query("SELECT * FROM all_posts ORDER BY page_id ASC")
    fun getAll(): Flow<List<AllPost>>

    @Query("SELECT * FROM all_posts WHERE id = :id")
    fun getPost(id: String): Flow<AllPost?>

    @Query("DELETE FROM all_posts")
    fun deleteAll()
}