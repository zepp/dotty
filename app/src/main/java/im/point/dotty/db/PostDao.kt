package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import im.point.dotty.model.Post
import io.reactivex.Flowable

interface PostDao<in T : Post> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(post: T)
}