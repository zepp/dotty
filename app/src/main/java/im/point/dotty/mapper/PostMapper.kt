/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.CompletePost
import im.point.dotty.model.Post
import im.point.dotty.network.RawPost

class PostMapper : MetaPostMapper<CompletePost<*>>(), Mapper<Post, RawPost> {
    override fun map(entry: RawPost): Post {
        with(entry) {
            val id = id ?: throw Exception("Post ID is not provided")
            val authorId = author?.id ?: throw Exception("Post's author ID is not provided")
            val login = author?.login ?: throw Exception("Post's author login is not provided")
            return mergeRawPost(Post(id, authorId, login), entry)
        }
    }
}