package im.point.dotty.mapper

import im.point.dotty.model.AllPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.RawPost

class AllPostMapper : PostMapper<AllPost>(), Mapper<AllPost, MetaPost> {
    override fun map(entry: MetaPost): AllPost {
        return mergeMetaPost(AllPost(entry.post?.id ?: throw Exception("Post id is not provided"),
                entry.post?.author?.id
                        ?: throw Exception("Post's author id is not provided")), entry)
    }

    override fun merge(model: AllPost, entry: MetaPost): AllPost {
        return mergeMetaPost(model, entry)
    }
}