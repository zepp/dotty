/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.db.CommentDao
import im.point.dotty.db.PostDao
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.RawPostMapper
import im.point.dotty.model.Comment
import im.point.dotty.model.Post
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CommentRepo<in T : Post>(private val api: PointAPI,
                               private val commentDao: CommentDao,
                               private val postDao: PostDao<T>,
                               private val id: String,
                               private var model: Flow<T?>,
                               private val mapper: Mapper<Comment, RawComment> = CommentMapper(),
                               private val postMapper: RawPostMapper<T> = RawPostMapper()) : Repository<Comment, String> {

    override fun getAll(): Flow<List<Comment>> {
        return commentDao.getPostComments(id)
    }

    override fun getItem(id: String): Flow<Comment> {
        val bits = id.split('/')
        return commentDao.geComment(bits.first(), bits.last().toLong())
                .map { it ?: throw Exception("comment not found") }
    }

    override fun fetch() = flow {
        with(api.getPost(id)) {
            checkSuccessful()
            postDao.insertItem(postMapper.merge(model.first() ?: throw Exception("post not found"),
                    post ?: throw Exception("invalid raw post")))
            comments?.let {
                val list = it.map { comment -> mapper.map(comment) }
                commentDao.insertAll(list)
                emit(list)
            }
        }
    }

    override fun purge() {
        commentDao.deleteAll()
    }
}