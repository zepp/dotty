/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.AllPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.PostFilesMapper
import im.point.dotty.model.AllPost
import im.point.dotty.model.CompleteAllPost
import im.point.dotty.model.PostFile
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import im.point.dotty.network.RawPost
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AllPostRepo(private val api: PointAPI,
                  private val state: AppState,
                  db: DottyDatabase,
                  private val mapper: Mapper<CompleteAllPost, MetaPost> = AllPostMapper(),
                  private val fileMapper: Mapper<List<PostFile>, RawPost> = PostFilesMapper())
    : Repository<CompleteAllPost, String> {
    private val metaPostDao = db.getAllPostDao()
    private val postDao = db.getPostDao()
    private val fileDao = db.getPostFileDao()

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean) = flow {
        metaPostDao.getAll().let { if (it.isNotEmpty()) emit(it) }
        with(api.getAll(if (isBefore) state.allPageId else null)) {
            checkSuccessful()
            with(posts.map { mapper.map(it) }) {
                if (size > 0) {
                    state.allPageId = last().metapost.pageId
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

    override fun getAll() = metaPostDao.getAllFlow()

    override fun getItem(id: String) =
            metaPostDao.getItemFlow(id).map { it ?: throw Exception("post not found") }

    override fun fetchAll() = fetch(false)

    fun getMetaPost(postId: String) = metaPostDao.getMetaPostFlow(postId)
            .map { (it ?: throw Exception("Post not found")) }

    fun updateMetaPost(post: AllPost) = metaPostDao.insertItem(post)

    fun fetchBefore() = fetch(true)
}