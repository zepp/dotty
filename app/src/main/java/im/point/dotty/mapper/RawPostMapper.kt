package im.point.dotty.mapper

import im.point.dotty.model.Post
import im.point.dotty.model.RecentPost
import im.point.dotty.network.RawPost

class RawPostMapper<T : Post> : PostMapper<T>(), Mapper<T, RawPost> {
    override fun map(entry: RawPost): T {
        throw Exception("raw post can not be mapped")
    }

    override fun merge(model: T, entry: RawPost): T {
        return mergeRawPost(model, entry)
    }
}