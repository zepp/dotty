package im.point.dotty.repository

import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.PostFilesMapper
import im.point.dotty.mapper.TaggedPostMapper
import im.point.dotty.model.CompleteTaggedPost
import im.point.dotty.model.PostFile
import im.point.dotty.model.TagLastPageId
import im.point.dotty.model.TaggedPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TaggedPostRepo(private val api: PointAPI,
                     db: DottyDatabase,
                     private val tag: String,
                     private val mapper: Mapper<CompleteTaggedPost, MetaPost> = TaggedPostMapper(tag),
                     private val fileMapper: Mapper<List<PostFile>, RawPost> = PostFilesMapper())
    : Repository<CompleteTaggedPost, String> {

    private val metaPostDao = db.getTaggedPostDao()
    private val pageIdDao = db.getTagLastPageIdDao()
    private val postDao = db.getPostDao()
    private val fileDao = db.getPostFileDao()

    private fun fetch(isBefore: Boolean) = flow {
        metaPostDao.getTaggedPosts(tag).let { if (it.isNotEmpty()) emit(it) }
        with(api.getTagged(tag, if (isBefore) pageIdDao.getTagLastPageId(tag) else null)) {
            checkSuccessful()
            with(posts.map { mapper.map(it) }) {
                if (size > 0) {
                    last().metapost.pageId?.let { pageIdDao.insertItem(TagLastPageId(tag, it)) }
                    postDao.insertAll(map { it.post })
                    metaPostDao.insertAll(map { it.metapost })
                    emit(this)
                }
            }
            fileDao.insertAll(posts
                .flatMap { fileMapper.map(it.post ?: throw Exception("raw post is null")) })
        }
    }

    override fun getAll(): Flow<List<CompleteTaggedPost>> = metaPostDao.getTaggedPostsFlow(tag)

    override fun getItem(id: String) = metaPostDao.getTaggedPostFlow(id, tag)
            .map { it ?: throw Exception("post not found") }

    override fun fetchAll() = fetch(false)

    fun fetchBefore() = fetch(true)

    fun getMetaPost(postId: String) = metaPostDao.getMetaPostFlow(postId, tag)
            .map { it ?: throw Exception("Post not found") }

    fun updateMetaPost(post: TaggedPost) = metaPostDao.insertItem(post)
}