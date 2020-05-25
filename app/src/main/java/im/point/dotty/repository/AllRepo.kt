package im.point.dotty.repository

import im.point.dotty.db.AllPostDao
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.AllPost
import im.point.dotty.network.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class AllRepo(private val api: PointAPI,
                       private val token: String,
                       private val allPostDao: AllPostDao,
                       private val mapper: Mapper<AllPost, MetaPost>)
    : Repository<AllPost> {

    override fun getAll(): Flowable<List<AllPost>> {
        return allPostDao.all
    }

    override fun fetch(): Single<List<AllPost>> {
        val source = Observable.create { emitter: ObservableEmitter<PostsReply> ->
            api.getAll(token, "").enqueue(ObservableCallBackAdapter(emitter)) }
        return source
                .observeOn(Schedulers.io())
                .flatMap { reply: PostsReply -> Observable.fromIterable(reply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }
                .toList()
                .doOnSuccess { allPosts: List<AllPost> -> allPostDao.insertAll(allPosts) }
    }

}