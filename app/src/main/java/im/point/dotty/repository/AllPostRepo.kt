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
                  private val allPostDao: AllPostDao,
                  private val mapper: Mapper<AllPost, MetaPost> = AllPostMapper())
    : Repository<AllPost, String> {

    @SuppressLint("CheckResult")
    fun fetch(isBefore: Boolean) = flow {
        with(api.getAll(if (isBefore) state.allPageId else null)) {
            checkSuccessful()
            posts?.let {
                val list = it.map { post -> mapper.map(post) }
                state.allPageId = list.last().pageId
                allPostDao.insertAll(list)
                emit(list)
            }
        }
    }

    override fun getAll(): Flow<List<AllPost>> {
        return allPostDao.getAll()
    }

    override fun getItem(id: String): Flow<AllPost> {
        return allPostDao.getPost(id).map { it ?: throw Exception("post not found") }
    }

    override fun fetchAll(): Flow<List<AllPost>> {
        return fetch(false)
    }

    override fun updateItem(model: AllPost) {
        allPostDao.insertItem(model)
    }

    override fun purge() {
        allPostDao.deleteAll()
    }

    fun fetchBefore(): Flow<List<AllPost>> {
        return fetch(true)
    }
}