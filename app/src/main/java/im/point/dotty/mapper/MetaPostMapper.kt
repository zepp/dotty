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

abstract class MetaPostMapper<T : CompletePost<*>> {
    protected val commentMapper = CommentMapper()

    protected fun mergeMetaPost(model: T, post: MetaPost): T {
        model.metapost.pageId = post.uid
        if (post.commentId == null) {
            // copy flags only if it is not recommended comment
            model.metapost.bookmarked = post.isBookmarked ?: false
            model.metapost.recommended = post.isRecommended ?: false
            model.metapost.subscribed = post.isSubscribed ?: false
        }
        mergeRawPost(model.post, post.post ?: throw Exception("Raw post is not provided"))
        return model
    }

    protected fun mergeRawPost(model: Post, post: RawPost): Post {
        model.isPinned = post.isPinned ?: false
        model.isPrivate = post.isPrivate ?: false
        model.authorLogin = post.author?.login
                ?: throw Exception("Post's author login is not provided")
        model.authorName = post.author?.name ?: ""
        model.text = post.text ?: throw Exception("Post's text is not provided")
        model.timestamp = post.created?.let { format.parse(it) }
        model.tags = post.tags ?: listOf()
        model.commentCount = post.commentsCount ?: 0
        return model
    }

    companion object {
        private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    }
}