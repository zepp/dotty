package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Ignore
import java.util.*

abstract class Post {
    var login: String? = null
    var name: String? = null

    @ColumnInfo(name = "page_id")
    var pageId: Long? = null
    var text: String? = null
    var tags: List<String>? = null
    var timestamp: Date? = null
    var commentCount: Int? = null

    @Ignore
    open val postId: String = ""

    val nameOrLogin: String?
        get() = if (name.isNullOrEmpty()) login else name
}