package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.RecentPost
import io.reactivex.Flowable

@Dao
interface RecentPostDao : PostDao<RecentPost> {
    @Query("SELECT * FROM recent_posts ORDER BY page_id DESC")
    fun getAll(): Flowable<List<RecentPost>>

    @Query("SELECT * FROM recent_posts WHERE id = :id")
    fun getPost(id: String): Flowable<RecentPost>

    @Query("DELETE FROM recent_posts")
    fun deleteAll()
}