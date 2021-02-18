package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.CommentedPost
import io.reactivex.Flowable

@Dao
interface CommentedPostDao : PostDao<CommentedPost>{
    @Query("SELECT * FROM commented_posts ORDER BY page_id DESC")
    fun getAll(): Flowable<List<CommentedPost>>

    @Query("SELECT * FROM commented_posts WHERE id = :id")
    fun getPost(id: String): Flowable<CommentedPost>

    @Query("DELETE FROM commented_posts")
    fun deleteAll()
}