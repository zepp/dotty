package im.point.dotty.repository

import im.point.dotty.db.RecentPostDao
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.ObservableCallBackAdapter
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostsReply
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class RecentRepo(private val api: PointAPI,
                          private val token: String,
                          private val recentPostDao: RecentPostDao,
                          private val mapper: Mapper<RecentPost, MetaPost?>)
    : Repository<RecentPost> {

    override fun getAll(): Flowable<List<RecentPost>> {
        return recentPostDao.all
    }

    override fun fetch(): Single<List<RecentPost>> {
        val source = Observable.create { emitter: ObservableEmitter<PostsReply> -> api.getRecent(token, "").enqueue(ObservableCallBackAdapter(emitter)) }
        return source
                .observeOn(Schedulers.io())
                .flatMap { postsReply: PostsReply -> Observable.fromIterable(postsReply.posts) }
                .map { entry: MetaPost? -> mapper.map(entry) }
                .toList()
                .doOnSuccess { recentPosts: List<RecentPost> -> recentPostDao.insertAll(recentPosts) }
    }

}