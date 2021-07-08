/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.mapper.TagMapper
import im.point.dotty.mapper.UserMapper
import im.point.dotty.model.User
import im.point.dotty.network.PointAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepo(private val api: PointAPI,
               private val state: AppState,
               db: DottyDatabase,
               private val mapper: UserMapper = UserMapper()) : Repository<User, Long> {

    private val userDao = db.getUserDao()
    private val tagDao = db.getUserTagDao()

    override fun getAll(): Flow<List<User>> {
        return userDao.getAllFlow()
    }

    override fun getItem(id: Long): Flow<User> {
        return userDao.getItemFlow(id).map { it ?: throw Exception("user not found in DB") }
    }

    override fun fetchAll(): Flow<List<User>> {
        throw Exception("operation is not supported")
    }

    fun fetchUser(id: Long) = flow {
        userDao.getItem(id)?.let { emit(it) }
        val user = with(api.getUser(id)) {
            checkSuccessful()
            with(mapper.map(this)) {
                userDao.insertItem(this)
                emit(this)
                this
            }
        }
        with(api.getUserTags(user.login)) {
            val mapper = TagMapper(user.id)
            tagDao.insertAll(map { mapper.map(it) })
        }
    }

    fun fetchUser(login: String) = flow {
        with(api.getUser(login)) {
            checkSuccessful()
            with(mapper.map(this)) {
                userDao.insertItem(this)
                emit(this)
            }
        }
    }

    fun fetchMe() = flow {
        userDao.getItem(state.id)?.let { emit(it) }
        with(api.getUser(state.id)) {
            checkSuccessful()
            with(mapper.map(this)) {
                userDao.insertItem(this)
                emit(this)
            }
        }
    }

    fun getMe() = getItem(state.id)
}