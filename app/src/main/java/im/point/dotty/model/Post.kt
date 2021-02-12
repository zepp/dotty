package im.point.dotty.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

abstract class Post {
    var login: String? = null
    var name: String? = null
    @ColumnInfo(name = "page_id")
    var pageId: Long? = null
    var text: String? = null
    var timestamp: Date? = null

    @Ignore
    open val postId:String = ""
}