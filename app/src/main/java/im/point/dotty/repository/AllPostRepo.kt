/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.AllPostDao
import im.point.dotty.mapper.AllPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.AllPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostsReply
import im.point.dotty.network.SingleCallbackAdapter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class AllPostRepo(private val api: PointAPI,
                  private val state: AppState,
                  private val allPostDao: AllPostDao,
                  private val mapper: Mapper<AllPost, MetaPost> = AllPostMapper())
    : Repository<AllPost, String> {

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean): Single<List<AllPost>> {
        val source = Single.create<PostsReply> { emitter ->
            api.getAll(state.token, if (isBefore) state.allPageId else null)
                    .enqueue(SingleCallbackAdapter(emitter))
        }
                .flatMapObservable { reply: PostsReply -> Observable.fromIterable(reply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }
        source.lastElement().subscribe { post -> state.allPageId = post.pageId }
        return source.toList()
                .doOnSuccess { allPosts: List<AllPost> -> allPostDao.insertAll(allPosts) }
    }

    override fun getAll(): Flowable<List<AllPost>> {
        return allPostDao.getAll()
    }

    override fun getItem(id: String): Flowable<AllPost> {
        return allPostDao.getPost(id)
    }

    override fun fetch(): Single<List<AllPost>> {
        return fetch(false)
    }

    override fun purge() {
        allPostDao.deleteAll()
    }

    fun fetchBefore(): Single<List<AllPost>> {
        return fetch(true)
    }
}