package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.CompleteTaggedPost
import im.point.dotty.model.TaggedPost
import kotlinx.coroutines.flow.Flow

@Dao
interface TaggedPostDao : CommonDao<TaggedPost> {
    @Query("SELECT * FROM tagged_posts")
    fun getAllPostFlow(): Flow<List<CompleteTaggedPost>>

    @Query("SELECT * FROM tagged_posts WHERE tag = :tag")
    fun getTaggedPosts(tag: String): List<CompleteTaggedPost>

    @Query("SELECT * FROM tagged_posts WHERE tag = :tag")
    fun getTaggedPostsFlow(tag: String): Flow<List<CompleteTaggedPost>>

    @Query("SELECT * FROM tagged_posts WHERE id = :id AND tag = :tag")
    fun getTaggedPostFlow(id: String, tag: String): Flow<CompleteTaggedPost?>

    @Query("SELECT * FROM tagged_posts WHERE id = :id AND tag = :tag")
    fun getMetaPostFlow(id: String, tag: String): Flow<TaggedPost?>
}