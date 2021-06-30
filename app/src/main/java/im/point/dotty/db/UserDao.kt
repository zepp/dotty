/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : CommonDao<User> {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getItem(id: Long): User?

    @Query("SELECT * FROM users")
    fun getAllFlow(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getItemFlow(id: Long): Flow<User?>
}