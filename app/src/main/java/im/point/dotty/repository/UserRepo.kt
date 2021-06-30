/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.UserDao
import im.point.dotty.mapper.UserMapper
import im.point.dotty.model.User
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepo(private val api: PointAPI,
               private val state: AppState,
               private val dao: UserDao,
               private val mapper: UserMapper = UserMapper()) : Repository<User, Long> {
    override fun getAll(): Flow<List<User>> {
        return dao.getAllFlow()
    }

    override fun getItem(id: Long): Flow<User> {
        return dao.getItemFlow(id).map { it ?: throw Exception("user not found in DB") }
    }

    override fun fetchAll(): Flow<List<User>> {
        throw Exception("operation is not supported")
    }

    fun fetchUser(id: Long) = flow {
        dao.getItem(id)?.let { emit(it) }
        with(api.getUser(id)) {
            checkSuccessful()
            with(mapper.map(this)) {
                dao.insertItem(this)
                emit(this)
            }
        }
    }

    fun fetchMe() = flow {
        with(state.id) {
            if (this != null) {
                dao.getItem(this)?.let { emit(it) }
            }
        }
        with(api.getMe()) {
            checkSuccessful()
            with(mapper.map(this)) {
                state.id = id
                dao.insertItem(this)
                emit(this)
            }
        }
    }

    fun getMe(): Flow<User> {
        return getItem(state.id ?: throw Exception("user ID is empty"));
    }
}