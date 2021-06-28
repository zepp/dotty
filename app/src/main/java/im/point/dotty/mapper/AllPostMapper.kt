/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.AllPost
import im.point.dotty.network.MetaPost

class AllPostMapper : PostMapper<AllPost>(), Mapper<AllPost, MetaPost> {
    override fun map(entry: MetaPost): AllPost {
        return mergeMetaPost(entry.post?.let {
            AllPost(it.id ?: throw Exception("Post ID is not provided"),
                    it.author?.id ?: throw Exception("Post's author id is not provided"),
                    it.author?.login ?: throw Exception("Post's author login is not provided"))
        }
                ?: throw Exception("Post is empty"), entry)
    }

    override fun merge(model: AllPost, entry: MetaPost): AllPost {
        return mergeMetaPost(model, entry)
    }
}