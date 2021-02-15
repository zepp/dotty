package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.point.dotty.model.AllPost
import io.reactivex.Flowable

@Dao
interface AllPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<AllPost>)

    @Query("SELECT * FROM all_posts ORDER BY page_id DESC")
    fun getAll() : Flowable<List<AllPost>>

    @Query("SELECT * FROM all_posts WHERE id = :id")
    fun getPost(id: Long): Flowable<AllPost>
}