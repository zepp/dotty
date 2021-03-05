/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.UserDao
import im.point.dotty.mapper.UserMapper
import im.point.dotty.model.User
import im.point.dotty.network.PointAPI
import im.point.dotty.network.SingleCallbackAdapter
import im.point.dotty.network.UserReply
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter

class UserRepo(private val api: PointAPI,
               private val state: AppState,
               private val dao: UserDao,
               private val mapper: UserMapper = UserMapper()) : Repository<User, Long> {
    override fun getAll(): Flowable<List<User>> {
        return dao.getAll()
    }

    override fun getItem(id: Long): Flowable<User> {
        return dao.getUser(id)
    }

    override fun fetch(): Single<List<User>> {
        throw Exception("operation is not supported")
    }

    override fun purge() {
        dao.deleteAll()
    }

    fun fetchUser(id: Long): Single<User> {
        return Single.create { emitter: SingleEmitter<UserReply> ->
            api.getUser(state.token, id)
                    .enqueue(SingleCallbackAdapter(emitter))
        }
                .map { user -> mapper.map(user) }
                .doOnSuccess { user -> dao.insertUser(user) }
    }

    fun fetchMe(): Single<User> {
        return Single.create { emitter: SingleEmitter<UserReply> ->
            api.getMe(state.token)
                    .enqueue(SingleCallbackAdapter(emitter))
        }
                .map { user -> mapper.map(user) }
                .doOnSuccess { user ->
                    state.id = user.id
                    dao.insertUser(user)
                }
    }

    fun getMe(): Flowable<User> {
        return if (state.id == null) {
            fetchMe().flatMapPublisher { user -> dao.getUser(user.id) }
        } else {
            dao.getUser(state.id ?: throw Exception("user ID is not initialized"))
        }
    }
}