package im.point.dotty.repository

import im.point.dotty.db.CommentedPostDao
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

internal class CommentedRepo(private val api: PointAPI,
                             private val token: String,
                             private val commentedPostDao: CommentedPostDao,
                             private val mapper: Mapper<CommentedPost, MetaPost?>)
    : Repository<CommentedPost> {

    override fun getAll(): Flowable<List<CommentedPost>> {
        return commentedPostDao.all
    }

    override fun fetch(): Single<List<CommentedPost>> {
        val source = Observable.create { emitter: ObservableEmitter<PostsReply> -> api.getComments(token, "").enqueue(ObservableCallBackAdapter(emitter)) }
        return source
                .observeOn(Schedulers.io())
                .flatMap { reply: PostsReply -> Observable.fromIterable(reply.posts) }
                .map { entry: MetaPost? -> mapper.map(entry) }
                .toList()
                .doOnSuccess { commentedPosts: List<CommentedPost> -> commentedPostDao.insertAll(commentedPosts) }
    }

}