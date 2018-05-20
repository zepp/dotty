package im.point.dotty.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import im.point.dotty.model.Comment;
import io.reactivex.Flowable;

@Dao
public interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addComments(Comment... comments);

    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY timestamp")
    Flowable<List<Comment>> getComments(String postId);
}
