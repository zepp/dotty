/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.CommentedPostDao
import im.point.dotty.mapper.CommentedPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.CommentedPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostsReply
import im.point.dotty.network.SingleCallbackAdapter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class CommentedPostRepo(private val api: PointAPI,
                        private val state: AppState,
                        private val commentedPostDao: CommentedPostDao,
                        private val mapper: Mapper<CommentedPost, MetaPost> = CommentedPostMapper())
    : Repository<CommentedPost, String> {

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean): Single<List<CommentedPost>> {
        val source = Single.create<PostsReply> { emitter ->
            api.getComments(state.token, if (isBefore) state.commentedPageId else null)
                    .enqueue(SingleCallbackAdapter(emitter))
        }
                .flatMapObservable { reply: PostsReply -> Observable.fromIterable(reply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }
        source.lastElement().subscribe { post -> state.commentedPageId = post.pageId }
        return source.toList()
                .doOnSuccess { commentedPosts: List<CommentedPost> -> commentedPostDao.insertAll(commentedPosts) }
    }

    override fun getAll(): Flowable<List<CommentedPost>> {
        return commentedPostDao.getAll()
    }

    override fun getItem(id: String): Flowable<CommentedPost> {
        return commentedPostDao.getPost(id)
    }

    override fun fetch(): Single<List<CommentedPost>> {
        return fetch(false)
    }

    fun fetchBefore(): Single<List<CommentedPost>> {
        return fetch(true)
    }

    override fun purge() {
        commentedPostDao.deleteAll()
    }
}