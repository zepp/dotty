package im.point.dotty.repository

import im.point.dotty.db.UserDao
import im.point.dotty.db.UserPostDao
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.UserPostMapper
import im.point.dotty.model.UserPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserPostRepo(private val api: PointAPI,
                   private val userDao: UserDao,
                   private val userPostDao: UserPostDao,
                   private val userId: Long,
                   private val mapper: Mapper<UserPost, MetaPost> = UserPostMapper(userId)) : Repository<UserPost, String> {

    override fun getAll(): Flow<List<UserPost>> {
        return userPostDao.getUserPostsFlow(userId)
    }

    override fun getItem(id: String): Flow<UserPost> {
        return userPostDao.getItemFlow(id).map { it ?: throw Exception("post not found") }
    }

    override fun fetchAll() = flow {
        val login = userDao.getItem(userId)?.login ?: throw Exception("user login is empty")
        userPostDao.getUserPosts(userId).let { if (it.isNotEmpty()) emit(it) }
        with(api.getUserPosts(login, null)) {
            checkSuccessful()
            posts?.let {
                val list = it.map { post -> mapper.map(post) }
                userPostDao.insertAll(list)
                emit(list)
            }
        }
    }

    override fun updateItem(model: UserPost) {
        userPostDao.insertItem(model)
    }
}