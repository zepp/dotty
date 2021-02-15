package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.point.dotty.model.RecentPost
import io.reactivex.Flowable

@Dao
interface RecentPostDao : PostDao<RecentPost> {
    @Query("SELECT * FROM recent_posts ORDER BY page_id DESC")
    fun getAll(): Flowable<List<RecentPost>>

    @Query("SELECT * FROM recent_posts WHERE id = :id")
    fun getPost(id: Long): Flowable<RecentPost>
}