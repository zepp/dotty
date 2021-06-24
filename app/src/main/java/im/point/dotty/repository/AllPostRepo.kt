/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.annotation.SuppressLint
import im.point.dotty.common.AppState
import im.point.dotty.db.AllPostDao
import im.point.dotty.mapper.AllPostMapper
import im.point.dotty.mapper.Mapper
import im.point.dotty.model.AllPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AllPostRepo(private val api: PointAPI,
                  private val state: AppState,
                  private val dao: AllPostDao,
                  private val mapper: Mapper<AllPost, MetaPost> = AllPostMapper())
    : Repository<AllPost, String> {

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean) = flow {
        dao.getAll().let { if (it.isNotEmpty()) emit(it) }
        with(api.getAll(if (isBefore) state.allPageId else null)) {
            checkSuccessful()
            posts?.let {
                val list = it.map { post -> mapper.map(post) }
                state.allPageId = list.last().pageId
                dao.insertAll(list)
                emit(list)
            }
        }
    }

    override fun getAll(): Flow<List<AllPost>> {
        return dao.getAllFlow()
    }

    override fun getItem(id: String): Flow<AllPost> {
        return dao.getItemFlow(id).map { it ?: throw Exception("post not found") }
    }

    override fun fetchAll(): Flow<List<AllPost>> {
        return fetch(false)
    }

    override fun updateItem(model: AllPost) {
        dao.insertItem(model)
    }

    fun fetchBefore(): Flow<List<AllPost>> {
        return fetch(true)
    }
}