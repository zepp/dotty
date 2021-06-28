/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.CommentedPost
import im.point.dotty.network.MetaPost

class CommentedPostMapper : PostMapper<CommentedPost>(), Mapper<CommentedPost, MetaPost> {
    override fun map(entry: MetaPost): CommentedPost {
        return mergeMetaPost(entry.post?.let {
            CommentedPost(it.id ?: throw Exception("Post ID is not provided"),
                    it.author?.id ?: throw Exception("Post's author id is not provided"),
                    it.author?.login ?: throw Exception("Post's author login is not provided"))
        }
                ?: throw Exception("Post is empty"), entry)
    }

    override fun merge(model: CommentedPost, entry: MetaPost): CommentedPost {
        return mergeMetaPost(model, entry)
    }
}