/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.CompleteRecentPost
import im.point.dotty.model.Post
import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost

class RecentPostMapper : MetaPostMapper<CompleteRecentPost>(), Mapper<CompleteRecentPost, MetaPost> {
    override fun map(entry: MetaPost): CompleteRecentPost {
        with(entry.post ?: throw Exception("Raw post is not provided")) {
            val id = id ?: throw Exception("Post ID is not provided")
            val authorId = author?.id ?: throw Exception("Post's author ID is not provided")
            val login = author?.login ?: throw Exception("Post's author login is not provided")
            return mergeMetaPost(CompleteRecentPost(RecentPost(id, authorId),
                    Post(id, authorId, login)), entry)
        }
    }
}