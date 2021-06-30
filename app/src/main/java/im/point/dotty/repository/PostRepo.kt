/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.PostMapper
import im.point.dotty.model.Comment
import im.point.dotty.model.Post
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawComment
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PostRepo(
        private val api: PointAPI,
        db: DottyDatabase,
        private val postMapper: PostMapper = PostMapper(),
        private val commentMapper: Mapper<Comment, RawComment> = CommentMapper()
) : Repository<Post, String> {

    private val postDao = db.getPostDao()
    private val commentDao = db.getCommentDao()

    fun fetchPostAndComments(id: String) = flow {
        postDao.getItem(id)?.let { emit(it) }
        with(api.getPost(id)) {
            checkSuccessful()
            with(comments.map { commentMapper.map(it) }) {
                commentDao.insertAll(this)
            }
            with(postMapper.map(post)) {
                postDao.insertItem(this)
                emit(this)
            }
        }
    }

    fun getPostComments(postId: String) =
            commentDao.getPostCommentsFlow(postId)

    override fun getAll() = postDao.getAllFlow()

    override fun fetchAll() = throw Exception("not implemented")

    override fun getItem(id: String) =
            postDao.getItemFlow(id).map { it ?: throw Exception("post is not found") }
}