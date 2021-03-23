/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.point.dotty.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Comment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(comment: Comment)

    @Query("SELECT * FROM comments WHERE post_id = :id ORDER BY id")
    fun getPostComments(id: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE post_id = :postId AND id = :id")
    fun geComment(postId: String, id: Long): Flow<Comment?>

    @Query("DELETE FROM comments")
    fun deleteAll()
}