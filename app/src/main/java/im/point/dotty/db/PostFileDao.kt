package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.PostFile
import kotlinx.coroutines.flow.Flow

@Dao
interface PostFileDao : CommonDao<PostFile> {
    @Query("SELECT * FROM post_files WHERE post_id = :id")
    fun getPostFiles(id: String): Flow<List<PostFile>>

    @Query("SELECT * FROM post_files WHERE id = :id")
    fun getItem(id: String): PostFile?
}