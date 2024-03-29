/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.Comment
import im.point.dotty.network.RawComment
import java.text.SimpleDateFormat
import java.util.*

class CommentMapper : Mapper<Comment, RawComment> {
    override fun map(comment: RawComment): Comment {
        val result = Comment(
            "${comment.postId}/${comment.id}",
            comment.postId ?: throw Exception("Post id is not provided"),
            comment.author?.id ?: throw Exception("Comment's author id is not provided"),
            comment.id ?: throw Exception("Comment id is not provided"),
            comment.author?.login ?: throw Exception("Comment's author login is not provided")
        )
        return merge(result, comment)
    }

    private fun merge(model: Comment, comment: RawComment): Comment {
        model.replyTo = comment.toCommentId
        model.text = comment.text
        model.timestamp = format.parse(comment.created)
        model.name = comment.author?.name
        return model
    }

    companion object {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    }
}