package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import im.point.dotty.model.Comment
import io.reactivex.Flowable

@Dao
interface CommentDao {
    @Insert
    fun insertAll(items:List<Comment>)

    @Query("SELECT * FROM comments WHERE post_id = :id")
    fun getPostComments(id:String) : Flowable<List<Comment>>
}