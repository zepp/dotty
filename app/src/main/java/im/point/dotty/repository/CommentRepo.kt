package im.point.dotty.repository

import im.point.dotty.db.CommentDao
import im.point.dotty.db.PostDao
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.RawPostMapper
import im.point.dotty.model.Comment
import im.point.dotty.model.Post
import im.point.dotty.network.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CommentRepo<T : Post>(private val api: PointAPI,
                            private val state: AppState,
                            private val commentDao: CommentDao,
                            private val postDao: PostDao<T>,
                            private var model: T,
                            private val mapper: Mapper<Comment, RawComment> = CommentMapper(),
                            private val postMapper: RawPostMapper<T> = RawPostMapper()) : Repository<Comment> {

    override fun getAll(): Flowable<List<Comment>> {
        return commentDao.getPostComments(model.postId)
    }

    override fun fetch(): Single<List<Comment>> {
        val source = Observable.create { emitter: ObservableEmitter<PostReply> ->
            api.getPost(state.token ?: throw Exception("invalid token"), model.postId)
                    .enqueue(ObservableCallBackAdapter(emitter))
        }
        return source
                .observeOn(Schedulers.io())
                .doOnNext { postReply ->
                    postDao.insertItem(postMapper.merge(model, postReply.post
                            ?: throw Exception("invalid raw post")))
                }
                .flatMap { postReply -> Observable.fromIterable(postReply.comments) }
                .map { entry: RawComment -> mapper.map(entry) }
                .toList()
                .doOnSuccess { comments: List<Comment> -> commentDao.insertAll(comments) }
    }
}