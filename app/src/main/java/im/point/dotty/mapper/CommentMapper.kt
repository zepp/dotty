package im.point.dotty.mapper

import im.point.dotty.model.Comment
import im.point.dotty.network.RawComment
import java.text.SimpleDateFormat
import java.util.*

class CommentMapper : Mapper<Comment, RawComment> {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun map(comment: RawComment): Comment {
        val result = Comment(comment.postId ?: throw Exception("Post id is not provided"),
                comment.id ?: throw Exception("Comment id is not provided"),
                comment.author?.id ?: throw Exception("Comment's author id is not provided"))
        result.parentId = comment.toCommentId
        result.text = comment.text
        result.timestamp = format.parse(comment.created)
        return result
    }
}