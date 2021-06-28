/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost

class RecentPostMapper : PostMapper<RecentPost>(), Mapper<RecentPost, MetaPost> {
    override fun map(entry: MetaPost): RecentPost {
        return mergeMetaPost(entry.post?.let {
            RecentPost(it.id ?: throw Exception("Post ID is not provided"),
                    it.author?.id ?: throw Exception("Post's author id is not provided"),
                    it.author?.login ?: throw Exception("Post's author login is not provided"))
        }
                ?: throw Exception("Post is empty"), entry)
    }

    override fun merge(model: RecentPost, entry: MetaPost): RecentPost {
        return mergeMetaPost(model, entry)
    }
}