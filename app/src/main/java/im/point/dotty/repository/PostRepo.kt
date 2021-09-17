/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.digest
import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.PostMapper
import im.point.dotty.model.Comment
import im.point.dotty.model.Post
import im.point.dotty.model.PostFile
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawComment
import im.point.dotty.network.RawPost
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

class PostRepo(
        private val api: PointAPI,
        db: DottyDatabase,
        private val postMapper: PostMapper = PostMapper(),
        private val commentMapper: Mapper<Comment, RawComment> = CommentMapper()
) : Repository<Post, String> {
    private val sha1 = MessageDigest.getInstance("SHA-1")
    private val postDao = db.getPostDao()
    private val commentDao = db.getCommentDao()
    private val fileDao = db.getPostFileDao()

    internal suspend fun fetchPostAndComment(id: String, number: Int): Pair<RawPost, RawComment> {
        with(api.getPost(id)) {
            checkSuccessful()
            with(comments.map { commentMapper.map(it) }) {
                commentDao.insertAll(this)
            }
            return post to (comments.find { it.id == number }
                    ?: throw Exception("comment not found"))
        }
    }

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
            post.files?.run {
                fileDao.insertAll(map { url ->
                    PostFile(url.digest(sha1), id, url)
                })
            }
        }
    }

    fun getPostComments(postId: String) =
            commentDao.getPostCommentsFlow(postId)

    override fun getAll() = postDao.getAllFlow()

    override fun fetchAll() = throw Exception("not implemented")

    override fun getItem(id: String) =
            postDao.getItemFlow(id).map { it ?: throw Exception("post is not found") }

    fun removeComment(id: String) {
        commentDao.removeItem(id)
    }
}