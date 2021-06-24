/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.CommentedPostDao
import im.point.dotty.mapper.CommentedPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.CommentedPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CommentedPostRepo(private val api: PointAPI,
                        private val state: AppState,
                        private val dao: CommentedPostDao,
                        private val mapper: Mapper<CommentedPost, MetaPost> = CommentedPostMapper())
    : Repository<CommentedPost, String> {

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean) = flow {
        dao.getAll().let { if (it.isNotEmpty()) emit(it) }
        with(api.getComments(if (isBefore) state.commentedPageId else null)) {
            checkSuccessful()
            posts?.let {
                val list = it.map { post -> mapper.map(post) }
                if (!list.isEmpty()) {
                    dao.insertAll(list)
                    state.commentedPageId = list.last().pageId
                }
                emit(list)
            }
        }
    }

    override fun getAll(): Flow<List<CommentedPost>> {
        return dao.getAllFlow()
    }

    override fun getItem(id: String): Flow<CommentedPost> {
        return dao.getItemFlow(id)
                .map { it ?: throw Exception("post not found") }
    }

    override fun fetchAll(): Flow<List<CommentedPost>> {
        return fetch(false)
    }

    fun fetchBefore(): Flow<List<CommentedPost>> {
        return fetch(true)
    }

    override fun updateItem(model: CommentedPost) {
        dao.insertItem(model)
    }
}