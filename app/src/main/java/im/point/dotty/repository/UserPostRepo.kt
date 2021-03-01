package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.UserPostDao
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.UserPostMapper
import im.point.dotty.model.UserPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.ObservableCallBackAdapter
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostsReply
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single

class UserPostRepo(private val api: PointAPI,
                   private val state: AppState,
                   private val userPostDao: UserPostDao,
                   private val userId: Long,
                   private val mapper: Mapper<UserPost, MetaPost> = UserPostMapper()) : Repository<UserPost, String> {

    override fun getAll(): Flowable<List<UserPost>> {
        return userPostDao.getUserPosts(userId)
    }

    override fun getItem(id: String): Flowable<UserPost> {
        return userPostDao.getPost(id)
    }

    override fun fetch(): Single<List<UserPost>> {
        val source = Observable.create { emitter: ObservableEmitter<PostsReply> ->
            api.getRecent(state.token ?: throw Exception("invalid token"), null)
                    .enqueue(ObservableCallBackAdapter(emitter))
        }.flatMap { postsReply: PostsReply -> Observable.fromIterable(postsReply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }

        return source.toList()
                .doOnSuccess { recentPosts: List<UserPost> -> userPostDao.insertAll(recentPosts) }
    }

    override fun purge() {
        userPostDao.deleteAll()
    }
}