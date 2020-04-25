package im.point.dotty.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import im.point.dotty.model.UserPost;
import io.reactivex.Flowable;

@Dao
public interface UserPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPosts(List<UserPost> posts);

    @Query("SELECT * FROM user_posts WHERE user_id = :userId ORDER BY timestamp")
    Flowable<List<UserPost>> getUserPosts(long userId);

    @Query("SELECT * FROM user_posts WHERE id = :id")
    Flowable<UserPost> getPost(long id);
}
