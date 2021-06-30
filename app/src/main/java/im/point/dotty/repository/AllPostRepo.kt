/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.AllPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.AllPost
import im.point.dotty.model.CompleteAllPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AllPostRepo(private val api: PointAPI,
                  private val state: AppState,
                  db: DottyDatabase,
                  private val mapper: Mapper<CompleteAllPost, MetaPost> = AllPostMapper())
    : Repository<CompleteAllPost, String> {
    private val metaPostDao = db.getAllPostDao()
    private val postDao = db.getPostDao()

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean) = flow {
        metaPostDao.getAll().let { if (it.isNotEmpty()) emit(it) }
        with(api.getAll(if (isBefore) state.allPageId else null)) {
            checkSuccessful()
            with(posts.map { mapper.map(it) }) {
                if (size > 0) {
                    state.allPageId = last().metapost.pageId
                    metaPostDao.insertAll(map { it.metapost })
                    postDao.insertAll(map { it.post })
                    emit(this)
                }
            }
        }
    }

    override fun getAll() = metaPostDao.getAllFlow()

    override fun getItem(id: String) =
            metaPostDao.getItemFlow(id).map { it ?: throw Exception("post not found") }

    override fun fetchAll() = fetch(false)

    fun getMetaPost(postId: String) = metaPostDao.getMetaPostFlow(postId)
            .map { (it ?: throw Exception("Post not found")) }

    fun updateMetaPost(post: AllPost) = metaPostDao.insertItem(post)

    fun fetchBefore() = fetch(true)
}