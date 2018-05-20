package im.point.dotty.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import im.point.dotty.model.User;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface UserDao {
    @Insert
    void addUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    Observable<User> getUser(long id);
}
