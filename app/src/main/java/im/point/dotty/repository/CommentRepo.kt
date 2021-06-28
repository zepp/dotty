/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.db.CommentDao
import im.point.dotty.db.CommonDao
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.RawPostMapper
import im.point.dotty.model.Comment
import im.point.dotty.model.Post
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CommentRepo<in T : Post>(
    private val api: PointAPI,
    private val commentDao: CommentDao,
    private val postDao: CommonDao<T, String>,
    private val postId: String,
    private val commentMapper: Mapper<Comment, RawComment> = CommentMapper(),
    private val postMapper: RawPostMapper<T> = RawPostMapper()
) : Repository<Comment, String> {

    override fun getAll(): Flow<List<Comment>> =
            commentDao.getPostCommentsFlow(postId)

    override fun getItem(id: String) =
            commentDao.geItemFlow(id)
                .map { it ?: throw Exception("comment not found") }

    override fun fetchAll() = flow {
        commentDao.getPostComments(postId).let { if (it.isNotEmpty()) emit(it) }
        with(api.getPost(postId)) {
            checkSuccessful()
            postDao.insertItem(postMapper.merge(postDao.getItem(postId) ?: throw Exception("post not found"),
                    post ?: throw Exception("invalid raw post")))
            comments?.let {
                val list = it.map { comment -> commentMapper.map(comment) }
                commentDao.insertAll(list)
                emit(list)
            }
        }
    }

    override fun updateItem(model: Comment) =
        commentDao.insertItem(model)
}