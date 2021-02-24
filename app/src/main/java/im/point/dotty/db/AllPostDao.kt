package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.AllPost
import io.reactivex.Flowable

@Dao
interface AllPostDao : PostDao<AllPost>{
    @Query("SELECT * FROM all_posts ORDER BY page_id ASC")
    fun getAll(): Flowable<List<AllPost>>

    @Query("SELECT * FROM all_posts WHERE id = :id")
    fun getPost(id: String): Flowable<AllPost>

    @Query("DELETE FROM all_posts")
    fun deleteAll()
}