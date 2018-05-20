package im.point.dotty.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import im.point.dotty.model.AllPost;
import io.reactivex.Flowable;

@Dao
public interface AllPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPosts(List<AllPost> posts);

    @Query("SELECT * FROM all_posts ORDER BY timestamp")
    Flowable<List<AllPost>> getAllPosts();

    @Query("SELECT * FROM all_posts WHERE id = :id ORDER BY timestamp")
    Flowable<AllPost> getAllPosts(long id);
}
