package im.point.dotty.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
