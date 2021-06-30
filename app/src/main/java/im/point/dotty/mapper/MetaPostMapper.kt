/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.CompletePost
import im.point.dotty.model.Post
import im.point.dotty.network.MetaPost
import im.point.dotty.network.RawPost
import java.text.SimpleDateFormat
import java.util.*

open class MetaPostMapper<T : CompletePost<*>> {
    protected fun mergeMetaPost(model: T, post: MetaPost): T {
        model.metapost.pageId = post.uid
        model.metapost.bookmarked = post.isBookmarked == true
        model.metapost.recommended = post.isRecommended == true
        model.metapost.subscribed = post.isSubscribed == true
        mergeRawPost(model.post, post.post ?: throw Exception("invalid raw post"))
        return model
    }

    protected fun mergeRawPost(model: Post, post: RawPost): Post {
        model.pinned = post.isPinned
        model.private = post.isPrivate
        model.authorLogin = post.author?.login ?: throw Exception("invalid author login")
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