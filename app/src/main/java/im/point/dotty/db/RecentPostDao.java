package im.point.dotty.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import im.point.dotty.model.RecentPost;
import io.reactivex.Flowable;

@Dao
public interface RecentPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RecentPost> posts);

    @Query("SELECT * FROM recent_posts ORDER BY timestamp DESC")
    Flowable<List<RecentPost>> getAll();

    @Query("SELECT * FROM recent_posts WHERE id = :id")
    Flowable<RecentPost> getPost(long id);
}
