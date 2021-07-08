package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.UserData

@Dao
interface UserDataDao : CommonDao<UserData> {
    @Query("SELECT * FROM user_datum WHERE user_id = :userId")
    fun getItem(userId: Long): UserData?
}