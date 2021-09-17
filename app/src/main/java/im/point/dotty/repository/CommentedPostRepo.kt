/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.CommentedPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.PostFilesMapper
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.CompleteCommentedPost
import im.point.dotty.model.PostFile
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CommentedPostRepo(private val api: PointAPI,
                        private val state: AppState,
                        db: DottyDatabase,
                        private val postRepo: PostRepo,
                        private val mapper: CommentedPostMapper = CommentedPostMapper(),
                        private val fileMapper: Mapper<List<PostFile>, RawPost> = PostFilesMapper())
    : Repository<CompleteCommentedPost, String> {

    private val metaPostDao = db.getCommentedPostDao()
    private val postDao = db.getPostDao()
    private val fileDao = db.getPostFileDao()

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean) = flow {
        metaPostDao.getAll().let { if (it.isNotEmpty()) emit(it) }
        with(api.getComments(if (isBefore) state.commentedPageId else null)) {
            checkSuccessful()
            with(posts.map {
                if (it.commentId == null) {
                    mapper.map(it)
                } else {
                    with(postRepo.fetchPostAndComment(it.id, it.commentId!!)) {
                        mapper.map(it.apply { post = first }, second)
                    }
                }
            }) {
                if (size > 0) {
                    state.commentedPageId = last().metapost.pageId
                    postDao.insertAll(map { it.post })
                    metaPostDao.insertAll(map { it.metapost })
                    emit(this)
                }
            }
            fileDao.insertAll(posts
                    .flatMap { fileMapper.map(it.post ?: throw Exception("raw post is null")) })
        }
    }

    override fun getAll(): Flow<List<CompleteCommentedPost>> {
        return metaPostDao.getAllFlow()
    }

    override fun getItem(id: String): Flow<CompleteCommentedPost> {
        return metaPostDao.getItemFlow(id)
                .map { it ?: throw Exception("post not found") }
    }

    override fun fetchAll(): Flow<List<CompleteCommentedPost>> {
        return fetch(false)
    }

    fun fetchBefore(): Flow<List<CompleteCommentedPost>> {
        return fetch(true)
    }

    fun getMetaPost(postId: String) = metaPostDao.getMetaPostFlow(postId)
            .map { it ?: throw Exception("Post not found") }

    fun updateMetaPost(post: CommentedPost) = metaPostDao.insertItem(post)

    fun removeMetaPost(postId: String) = metaPostDao.removeItem(postId)
}