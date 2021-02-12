package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.db.CommentedPostDao
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.CommentedPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.ObservableCallBackAdapter
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostsReply
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CommentedRepo(private val api: PointAPI,
                             private val state: AppState,
                             private val commentedPostDao: CommentedPostDao,
                             private val mapper: Mapper<CommentedPost, MetaPost>)
    : Repository<CommentedPost> {

    @SuppressLint("CheckResult")
    fun fetch(isBefore : Boolean): Single<List<CommentedPost>> {
        val source = Observable.create { emitter: ObservableEmitter<PostsReply> ->
            api.getComments(state.token ?: throw Exception("invalid token"),
                    if (isBefore) state.commentedLastId else null)
                    .enqueue(ObservableCallBackAdapter(emitter)) }
                .observeOn(Schedulers.io())
                .flatMap { reply: PostsReply -> Observable.fromIterable(reply.posts) }
                .map { entry: MetaPost -> mapper.map(entry) }
        if (isBefore || state.commentedLastId == null) {
            source.lastElement().subscribe{post -> state.commentedLastId =post.id}
        }
        return source.toList()
                .doOnSuccess { commentedPosts: List<CommentedPost> -> commentedPostDao.insertAll(commentedPosts) }
    }

    override fun getAll(): Flowable<List<CommentedPost>> {
        return commentedPostDao.getAll()
    }

    override fun fetch(): Single<List<CommentedPost>> {
        return fetch(false)
    }

    fun fetchBefore() : Single<List<CommentedPost>> {
        return fetch(true)
    }
}