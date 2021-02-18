package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.point.dotty.model.UserPost
import io.reactivex.Flowable

@Dao
interface UserPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPosts(posts: List<UserPost>)

    @Query("SELECT * FROM user_posts WHERE user_id = :userId ORDER BY timestamp")
    fun getUserPosts(userId: Long): Flowable<List<UserPost>>

    @Query("SELECT * FROM user_posts WHERE id = :id")
    fun getPost(id: Long): Flowable<UserPost>

    @Query("DELETE FROM user_posts")
    fun deleteAll()
}