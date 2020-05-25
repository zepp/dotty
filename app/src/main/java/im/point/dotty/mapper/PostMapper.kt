package im.point.dotty.mapper

import im.point.dotty.model.Post
import im.point.dotty.network.MetaPost
import java.text.SimpleDateFormat
import java.util.*

open class PostMapper <T : Post> {
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    protected fun map(result: T, post: MetaPost): T {
        result.pageId = post.uid
        result.login = post.post?.author?.login
        result.name = post.post?.author?.name
        result.text = post.post?.text
        result.timestamp = post.post?.created?.let {format.parse(it)}
        return result
    }
}