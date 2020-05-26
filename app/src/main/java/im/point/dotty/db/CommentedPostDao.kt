package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.point.dotty.model.CommentedPost
import io.reactivex.Flowable

@Dao
interface CommentedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<CommentedPost>)

    @Query("SELECT * FROM commented_posts ORDER BY timestamp DESC")
    fun getAll(): Flowable<List<CommentedPost>>

    @Query("SELECT * FROM commented_posts WHERE id = :id")
    fun getPost(id: Long): Flowable<CommentedPost>
}