package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.point.dotty.model.Comment
import io.reactivex.Flowable

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Comment>)

    @Query("SELECT * FROM comments WHERE post_id = :id ORDER BY id")
    fun getPostComments(id: String): Flowable<List<Comment>>

    @Query("SELECT * FROM comments WHERE post_id = :postId AND id = :id")
    fun geComment(postId: String, id: Long): Flowable<Comment>
}