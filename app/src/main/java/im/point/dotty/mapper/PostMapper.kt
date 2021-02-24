package im.point.dotty.mapper

import im.point.dotty.model.Post
import im.point.dotty.network.MetaPost
import im.point.dotty.network.RawPost
import java.text.SimpleDateFormat
import java.util.*

open class PostMapper <T : Post> {
    protected fun mergeMetaPost(result: T, post: MetaPost): T {
        result.pageId = post.uid
        result.bookmarked = post.isBookmarked
        result.recommended = post.isRecommended
        return mergeRawPost(result, post.post ?: throw Exception("invalid raw post"))
    }

    protected fun mergeRawPost(model: T, post: RawPost): T {
        model.login = post.author?.login
        model.name = post.author?.name
        model.text = post.text
        model.timestamp = post.created?.let { format.parse(it) }
        model.tags = post.tags
        model.commentCount = post.commentsCount
        return model
    }

    companion object {
        private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    }
}