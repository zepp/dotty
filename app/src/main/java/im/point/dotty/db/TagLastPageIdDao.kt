package im.point.dotty.db

import androidx.room.Dao
import androidx.room.Query
import im.point.dotty.model.TagLastPageId

@Dao
interface TagLastPageIdDao : CommonDao<TagLastPageId> {
    @Query("SELECT page_id from TAG_LAST_PAGE_IDS WHERE tag = :tag")
    fun getTagLastPageId(tag: String): Long
}