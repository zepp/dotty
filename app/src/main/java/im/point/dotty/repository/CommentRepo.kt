/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.CommentDao
import im.point.dotty.db.PostDao
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.RawPostMapper
import im.point.dotty.model.Comment
import im.point.dotty.model.Post
import im.point.dotty.network.PointAPI
import im.point.dotty.network.PostReply
import im.point.dotty.network.RawComment
import im.point.dotty.network.SingleCallbackAdapter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class CommentRepo<in T : Post>(private val api: PointAPI,
                               private val state: AppState,
                               private val commentDao: CommentDao,
                               private val postDao: PostDao<T>,
                               private var model: Flowable<T>,
                               private val mapper: Mapper<Comment, RawComment> = CommentMapper(),
                               private val postMapper: RawPostMapper<T> = RawPostMapper()) : Repository<Comment, String> {

    override fun getAll(): Flowable<List<Comment>> {
        return model.firstElement().flatMapPublisher { model -> commentDao.getPostComments(model.id) }
    }

    override fun getItem(id: String): Flowable<Comment> {
        val bits = id.split('/')
        return commentDao.geComment(bits.first(), bits.last().toLong())
    }

    override fun fetch(): Single<List<Comment>> {
        return model.firstElement().flatMapSingle { model ->
            Single.create<PostReply> { emitter ->
                api.getPost(state.token, model.id)
                        .enqueue(SingleCallbackAdapter(emitter))
            }.doOnSuccess { postReply ->
                postDao.insertItem(postMapper.merge(model, postReply.post
                        ?: throw Exception("invalid raw post")))
            }
                    .flatMapObservable { postReply -> Observable.fromIterable(postReply.comments) }
                    .map { entry: RawComment -> mapper.map(entry) }
                    .toList()
                    .doOnSuccess { comments: List<Comment> -> commentDao.insertAll(comments) }
        }
    }

    override fun purge() {
        commentDao.deleteAll()
    }
}