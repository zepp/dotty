/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.RecentPostDao
import im.point.dotty.mapper.Mapper
import im.point.dotty.mapper.RecentPostMapper
import im.point.dotty.model.RecentPost
import im.point.dotty.network.MetaPost
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RecentPostRepo(private val api: PointAPI,
                     private val state: AppState,
                     private val recentPostDao: RecentPostDao,
                     private val mapper: Mapper<RecentPost, MetaPost> = RecentPostMapper()) :
        Repository<RecentPost, String> {

    private fun fetch(isBefore: Boolean) = flow {
        with(api.getRecent(state.token, if (isBefore) state.recentPageId else null)) {
            checkSuccessful()
            posts?.let {
                val list = it.map { post -> mapper.map(post) }
                state.recentPageId = list.last().pageId
                recentPostDao.insertAll(list)
                emit(list)
            }
        }
    }

    override fun getAll(): Flow<List<RecentPost>> {
        return recentPostDao.getAll()
    }

    override fun getItem(id: String): Flow<RecentPost> {
        return recentPostDao.getPost(id).map { it ?: throw Exception("post not found") }
    }

    override fun fetch(): Flow<List<RecentPost>> {
        return fetch(false)
    }

    override fun purge() {
        recentPostDao.deleteAll()
    }

    fun fetchBefore(): Flow<List<RecentPost>> {
        return fetch(true)
    }
}