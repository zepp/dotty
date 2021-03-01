/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.RecentPostDao
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.RecentPostMapper
import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.ObservableCallBackAdapter
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostsReply
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single

class RecentPostRepo(private val api: PointAPI,
                     private val state: AppState,
                     private val recentPostDao: RecentPostDao,
                     private val mapper: Mapper<RecentPost, MetaPost> = RecentPostMapper()) :
        Repository<RecentPost, String> {

    @SuppressLint("CheckResult")
    private fun fetch(isBefore: Boolean): Single<List<RecentPost>> {
        val source = Observable.create { emitter: ObservableEmitter<PostsReply> ->
            api.getRecent(state.token ?: throw Exception("invalid token"),
                    if (isBefore) state.recentPageId else null)
                    .enqueue(ObservableCallBackAdapter(emitter))
        }
                .flatMap { postsReply: PostsReply -> Observable.fromIterable(postsReply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }
        source.lastElement().subscribe { post -> state.recentPageId = post.pageId }
        return source.toList()
                .doOnSuccess { recentPosts: List<RecentPost> -> recentPostDao.insertAll(recentPosts) }
    }

    override fun getAll(): Flowable<List<RecentPost>> {
        return recentPostDao.getAll()
    }

    override fun getItem(id: String): Flowable<RecentPost> {
        return recentPostDao.getPost(id)
    }

    override fun fetch(): Single<List<RecentPost>> {
        return fetch(false)
    }

    override fun purge() {
        recentPostDao.deleteAll()
    }

    fun fetchBefore(): Single<List<RecentPost>> {
        return fetch(true)
    }
}