/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.UserTag
import kotlinx.coroutines.flow.Flow

@Dao
interface UserTagDao : CommonDao<UserTag> {
    @Query("SELECT * FROM user_tags WHERE user_id = :userId ORDER BY post_count DESC")
    fun getUserTags(userId: Long): List<UserTag>

    @Query("SELECT * FROM user_tags WHERE user_id = :userId")
    fun getUserTagsFlow(userId: Long): Flow<List<UserTag>>
}