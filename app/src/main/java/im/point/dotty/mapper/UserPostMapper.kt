/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.UserPost
import im.point.dotty.network.MetaPost

class UserPostMapper(private val userId: Long) : PostMapper<UserPost>(), Mapper<UserPost, MetaPost> {
    override fun map(entry: MetaPost): UserPost {
        return mergeMetaPost(entry.post?.let {
            UserPost(it.id ?: throw Exception("Post ID is not provided"),
                    it.author?.id ?: throw Exception("Post's author id is not provided"),
                    it.author?.login ?: throw Exception("Post's author login is not provided"),
                    userId)
        }
                ?: throw Exception("Post is empty"), entry)
    }

    override fun merge(model: UserPost, entry: MetaPost): UserPost {
        return mergeMetaPost(model, entry)
    }
}