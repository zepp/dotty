package im.point.dotty.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import im.point.dotty.model.AllPost;
import io.reactivex.Flowable;

@Dao
public interface AllPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AllPost> posts);

    @Query("SELECT * FROM all_posts ORDER BY timestamp DESC")
    Flowable<List<AllPost>> getAll();

    @Query("SELECT * FROM all_posts WHERE id = :id")
    Flowable<AllPost> getPost(long id);
}
