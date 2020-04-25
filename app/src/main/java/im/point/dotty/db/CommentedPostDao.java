package im.point.dotty.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import im.point.dotty.model.CommentedPost;
import io.reactivex.Flowable;

@Dao
public interface CommentedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CommentedPost> posts);

    @Query("SELECT * FROM commented_posts ORDER BY timestamp DESC")
    Flowable<List<CommentedPost>> getAll();

    @Query("SELECT * FROM commented_posts WHERE id = :id")
    Flowable<CommentedPost> getPost(long id);
}
