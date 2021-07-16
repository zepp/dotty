/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.PostFilesMapper
import im.point.dotty.mapper.RecentPostMapper
import im.point.dotty.model.CompleteRecentPost
import im.point.dotty.model.PostFile
import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RecentPostRepo(private val api: PointAPI,
                     private val state: AppState,
                     db: DottyDatabase,
                     private val mapper: Mapper<CompleteRecentPost, MetaPost> = RecentPostMapper(),
                     private val fileMapper: Mapper<List<PostFile>, RawPost> = PostFilesMapper()) :
        Repository<CompleteRecentPost, String> {

    private val metaPostDao = db.getRecentPostDao()
    private val postDao = db.getPostDao()
    private val fileDao = db.getPostFileDao()

    private fun fetch(isBefore: Boolean) = flow {
        metaPostDao.getAll().let { if (it.isNotEmpty()) emit(it) }
        with(api.getRecent(if (isBefore) state.recentPageId else null)) {
            checkSuccessful()
            with(posts.map { mapper.map(it) }) {
                if (size > 0) {
                    state.recentPageId = last().metapost.pageId
                    postDao.insertAll(map { it.post })
                    metaPostDao.insertAll(map { it.metapost })
                    emit(this)
                }
            }
            fileDao.insertAll(with(mutableListOf<PostFile>()) {
                for (post in posts.map { it.post ?: throw Exception("raw post is null") }) {
                    addAll(fileMapper.map(post))
                }
                toList()
            })
        }
    }

    override fun getAll(): Flow<List<CompleteRecentPost>> {
        return metaPostDao.getAllFlow()
    }

    override fun getItem(id: String): Flow<CompleteRecentPost> {
        return metaPostDao.getItemFlow(id).map { it ?: throw Exception("post not found") }
    }

    override fun fetchAll(): Flow<List<CompleteRecentPost>> {
        return fetch(false)
    }

    fun fetchBefore(): Flow<List<CompleteRecentPost>> {
        return fetch(true)
    }

    fun getMetaPost(postId: String) = metaPostDao.getMetaPostFlow(postId)
            .map { it ?: throw Exception("Post not found") }

    fun updateMetaPost(post: RecentPost) = metaPostDao.insertItem(post)
}