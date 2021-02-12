package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.db.AllPostDao
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.AllPost
import im.point.dotty.network.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class AllRepo(private val api: PointAPI,
                       private val state: AppState,
                       private val allPostDao: AllPostDao,
                       private val mapper: Mapper<AllPost, MetaPost>)
    : Repository<AllPost> {

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean): Single<List<AllPost>> {
        val source = Observable.create { emitter: ObservableEmitter<PostsReply> ->
            api.getAll(state.token ?: throw Exception("invalid token"), if (isBefore) state.allLastId else null)
                    .enqueue(ObservableCallBackAdapter(emitter)) }
                .observeOn(Schedulers.io())
                .flatMap { reply: PostsReply -> Observable.fromIterable(reply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }
        if (isBefore || state.allLastId == null) {
            source.lastElement().subscribe{post -> state.allLastId == post.id}
        }
        return source.toList()
                .doOnSuccess { allPosts: List<AllPost> -> allPostDao.insertAll(allPosts) }
    }

    override fun getAll(): Flowable<List<AllPost>> {
        return allPostDao.getAll()
    }

    override fun fetch(): Single<List<AllPost>> {
        return fetch(false)
    }

    fun fetchBefore(): Single<List<AllPost>> {
        return fetch(true)
    }
}