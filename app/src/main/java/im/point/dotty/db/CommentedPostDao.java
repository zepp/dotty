package im.point.dotty.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import im.point.dotty.model.CommentedPost;
import io.reactivex.Flowable;

@Dao
public interface CommentedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CommentedPost> posts);

    @Query("SELECT * FROM commented_posts ORDER BY timestamp")
    Flowable<List<CommentedPost>> getAll();

    @Query("SELECT * FROM commented_posts WHERE id = :id")
    Flowable<CommentedPost> getPost(long id);
}
