/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.CommentedPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.CompleteCommentedPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CommentedPostRepo(private val api: PointAPI,
                        private val state: AppState,
                        db: DottyDatabase,
                        private val mapper: Mapper<CompleteCommentedPost, MetaPost> = CommentedPostMapper())
    : Repository<CompleteCommentedPost, String> {

    private val metaPostDao = db.getCommentedPostDao()
    private val postDao = db.getPostDao()

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean) = flow {
        metaPostDao.getAll().let { if (it.isNotEmpty()) emit(it) }
        with(api.getComments(if (isBefore) state.commentedPageId else null)) {
            checkSuccessful()
            with(posts.map { mapper.map(it) }) {
                if (size > 0) {
                    state.commentedPageId = last().metapost.pageId
                    metaPostDao.insertAll(map { it.metapost })
                    postDao.insertAll(map { it.post })
                    emit(this)
                }
            }
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
}