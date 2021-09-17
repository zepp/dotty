/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.CommentedPost
import im.point.dotty.model.CompleteCommentedPost
import im.point.dotty.model.Post
import im.point.dotty.network.MetaPost
import im.point.dotty.network.RawComment

class CommentedPostMapper : MetaPostMapper<CompleteCommentedPost>(), Mapper<CompleteCommentedPost, MetaPost> {
    override fun map(entry: MetaPost): CompleteCommentedPost {
        with(entry.post ?: throw Exception("Raw post is not provided")) {
            val id = id ?: throw Exception("Post ID is not provided")
            val commentId = entry.commentId?.let { "$id/$it" }
            val authorId = author?.id ?: throw Exception("Post's author ID is not provided")
            val login = author?.login ?: throw Exception("Post's author login is not provided")
            return mergeMetaPost(CompleteCommentedPost(CommentedPost(id, authorId, commentId),
                    Post(id, authorId, login)), entry)
        }
    }

    fun map(post: MetaPost, comment: RawComment): CompleteCommentedPost {
        return map(post).apply { this.comment = commentMapper.map(comment) }
    }
}