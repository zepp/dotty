package im.point.dotty.repository

import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.UserPostMapper
import im.point.dotty.model.CompleteUserPost
import im.point.dotty.model.UserPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserPostRepo(private val api: PointAPI,
                   db: DottyDatabase,
                   private val userId: Long,
                   private val mapper: Mapper<CompleteUserPost, MetaPost> = UserPostMapper(userId))
    : Repository<CompleteUserPost, String> {

    private val userDao = db.getUserDao()
    private val metaPostDao = db.getUserPostsDao()
    private val postDao = db.getPostDao()

    override fun getAll(): Flow<List<CompleteUserPost>> {
        return metaPostDao.getUserPostsFlow(userId)
    }

    override fun getItem(id: String): Flow<CompleteUserPost> {
        return metaPostDao.getItemFlow(id, userId).map { it ?: throw Exception("post not found") }
    }

    override fun fetchAll() = flow {
        val login = userDao.getItem(userId)?.login ?: throw Exception("user login is empty")
        metaPostDao.getUserPosts(userId).let { if (it.isNotEmpty()) emit(it) }
        with(api.getUserPosts(login, null)) {
            checkSuccessful()
            with(posts.map { mapper.map(it) }) {
                if (size > 0) {
                    metaPostDao.insertAll(map { it.metapost })
                    postDao.insertAll(map { it.post })
                    emit(this)
                }
            }
        }
    }

    fun getMetaPost(postId: String) = metaPostDao.getMetaPostFlow(postId, userId)
            .map { it ?: throw Exception("Post not found") }

    fun updateMetaPost(post: UserPost) = metaPostDao.insertItem(post)
}