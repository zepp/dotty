package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.point.dotty.model.User
import io.reactivex.Flowable

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM users")
    fun getAll(): Flowable<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Long): Flowable<User>

    @Query("DELETE FROM users")
    fun deleteAll()
}