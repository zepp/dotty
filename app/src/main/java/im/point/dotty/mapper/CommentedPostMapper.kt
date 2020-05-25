package im.point.dotty.mapper

import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost

class CommentedPostMapper : PostMapper<CommentedPost>(), Mapper<CommentedPost, MetaPost> {
    override fun map(entry: MetaPost): CommentedPost {
        return map(CommentedPost(entry.post?.id ?: throw Exception("Post id is not provided"),
                entry.post?.author?.id ?: throw Exception("Post's author id is not provided")), entry)
    }
}