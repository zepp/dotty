package im.point.dotty.mapper

import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost

class RecentPostMapper : PostMapper<RecentPost>(), Mapper<RecentPost, MetaPost> {
    override fun map(entry: MetaPost): RecentPost {
        return mergeMetaPost(RecentPost(entry.post?.id
                ?: throw Exception("Post id is not provided"),
                entry.post?.author?.id
                        ?: throw Exception("Post's author id is not provided")), entry)
    }

    override fun merge(model: RecentPost, entry: MetaPost): RecentPost {
        return mergeMetaPost(model, entry)
    }
}