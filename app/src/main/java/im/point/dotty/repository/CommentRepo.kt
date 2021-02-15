package im.point.dotty.repository

import im.point.dotty.db.CommentDao
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.Comment
import im.point.dotty.network.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CommentRepo(private val api: PointAPI,
                  private val state: AppState,
                  private val commentDao: CommentDao,
                  private var id: String,
                  private val mapper: Mapper<Comment, RawComment> = CommentMapper()) : Repository<Comment> {

    override fun getAll(): Flowable<List<Comment>> {
        return commentDao.getPostComments(id)
    }

    override fun fetch(): Single<List<Comment>> {
        val source = Observable.create { emitter: ObservableEmitter<PostReply> ->
            api.getPost(state.token ?: throw Exception("invalid token"), id)
                    .enqueue(ObservableCallBackAdapter(emitter)) }
        return source
                .observeOn(Schedulers.io())
                .flatMap { postReply -> Observable.fromIterable(postReply.comments) }
                .map { entry: RawComment -> mapper.map(entry) }
                .toList()
                .doOnSuccess { comments: List<Comment> -> commentDao.insertAll(comments) }
    }
}