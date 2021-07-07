/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.CompleteTaggedPost
import im.point.dotty.model.Post
import im.point.dotty.model.TaggedPost
import im.point.dotty.network.MetaPost

class TaggedPostMapper(private val tag: String) : MetaPostMapper<CompleteTaggedPost>(), Mapper<CompleteTaggedPost, MetaPost> {
    override fun map(entry: MetaPost): CompleteTaggedPost {
        with(entry.post ?: throw Exception("Raw post is not provided")) {
            val id = id ?: throw Exception("Post ID is not provided")
            val authorId = author?.id ?: throw Exception("Post's author ID is not provided")
            val login = author?.login ?: throw Exception("Post's author login is not provided")
            return mergeMetaPost(CompleteTaggedPost(TaggedPost(id, authorId, tag),
                    Post(id, authorId, login)), entry)
        }
    }
}