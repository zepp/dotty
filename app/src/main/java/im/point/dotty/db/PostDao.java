package im.point.dotty.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import im.point.dotty.model.Post;
import io.reactivex.Observable;

@Dao
public interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPosts(Post... posts);

    @Query("SELECT * FROM posts WHERE user_id = :userId ORDER BY timestamp")
    Observable<List<Post>> getUserPosts(long userId);

    @Query("SELECT * FROM posts WHERE id = :id")
    Observable<Post> getPost(long id);
}
