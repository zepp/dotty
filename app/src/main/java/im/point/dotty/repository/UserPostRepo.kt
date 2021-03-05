package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.UserDao
import im.point.dotty.db.UserPostDao
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.UserPostMapper
import im.point.dotty.model.UserPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostsReply
import im.point.dotty.network.SingleCallbackAdapter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class UserPostRepo(private val api: PointAPI,
                   private val state: AppState,
                   private val userDao: UserDao,
                   private val userPostDao: UserPostDao,
                   private val userId: Long,
                   private val mapper: Mapper<UserPost, MetaPost> = UserPostMapper(userId)) : Repository<UserPost, String> {

    override fun getAll(): Flowable<List<UserPost>> {
        return userPostDao.getUserPosts(userId)
    }

    override fun getItem(id: String): Flowable<UserPost> {
        return userPostDao.getPost(id)
    }

    override fun fetch(): Single<List<UserPost>> {
        val source = userDao.getUser(userId).firstElement().flatMapSingle { user ->
            Single.create<PostsReply> { emitter ->
                api.getUserPosts(state.token, user.login
                        ?: throw Exception("user login is empty"), null)
                        .enqueue(SingleCallbackAdapter(emitter))
            }
        }.flatMapObservable { postsReply: PostsReply -> Observable.fromIterable(postsReply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }

        return source.toList()
                .doOnSuccess { recentPosts: List<UserPost> -> userPostDao.insertAll(recentPosts) }
    }

    override fun purge() {
        userPostDao.deleteAll()
    }
}