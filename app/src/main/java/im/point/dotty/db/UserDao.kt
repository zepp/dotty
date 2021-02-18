package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import im.point.dotty.model.User
import io.reactivex.Flowable

@Dao
interface UserDao {
    @Insert
    fun addUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Long): Flowable<User>

    @Query("DELETE FROM users")
    fun deleteAll()
}