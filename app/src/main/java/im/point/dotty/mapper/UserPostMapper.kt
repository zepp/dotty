/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.UserPost
import im.point.dotty.network.MetaPost

class UserPostMapper : PostMapper<UserPost>(), Mapper<UserPost, MetaPost> {
    override fun map(entry: MetaPost): UserPost {
        return mergeMetaPost(UserPost(entry.post?.id ?: throw Exception("Post id is not provided"),
                entry.post?.author?.id
                        ?: throw Exception("Post's author id is not provided")), entry)
    }

    override fun merge(model: UserPost, entry: MetaPost): UserPost {
        return mergeMetaPost(model, entry)
    }
}