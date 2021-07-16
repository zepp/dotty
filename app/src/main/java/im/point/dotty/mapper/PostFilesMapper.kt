package im.point.dotty.mapper

import im.point.dotty.common.digest
import im.point.dotty.model.PostFile
import im.point.dotty.network.RawPost
import java.security.MessageDigest

class PostFilesMapper : Mapper<List<PostFile>, RawPost> {
    private val sha1 = MessageDigest.getInstance("SHA-1")

    override fun map(post: RawPost) =
            post.files?.map {
                PostFile(it.digest(sha1), post.id ?: throw Exception("Post ID is not provided"), it)
            } ?: listOf()
}