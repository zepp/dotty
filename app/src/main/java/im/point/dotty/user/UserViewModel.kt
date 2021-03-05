/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.network.CompletableCallbackAdapter
import im.point.dotty.network.PointAPI
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers

class UserViewModel(application: DottyApplication, private val userId: Long) : AndroidViewModel(application) {
    private val userRepo = application.repoFactory.getUserRepo()
    private val userPostRepo = application.repoFactory.getUserPostRepo(userId)
    private val api: PointAPI = application.mainApi
    private val state: AppState = application.state
    private lateinit var actionsEmitter: SingleEmitter<Boolean>

    val isActionsVisible: Single<Boolean> = Single.create { emitter -> actionsEmitter = emitter }

    fun subscribe(): Completable {
        return getUser().flatMapCompletable { user ->
            Completable.create { emitter ->
                api.subscribeToUser(state.token, user.login
                        ?: throw Exception("user login is null"))
                        .enqueue(CompletableCallbackAdapter(emitter))
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun unsubscribe(): Completable {
        return getUser().flatMapCompletable { user ->
            Completable.create { emitter ->
                api.unsubscribeFromUser(state.token, user.login
                        ?: throw Exception("user login is null"))
                        .enqueue(CompletableCallbackAdapter(emitter))
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun subscribeRecommendations(): Completable {
        return getUser().flatMapCompletable { user ->
            Completable.create { emitter ->
                api.subscribeToUserRecommendations(state.token, user.login
                        ?: throw Exception("user login is null"))
                        .enqueue(CompletableCallbackAdapter(emitter))
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun unsubscribeRecommendations(): Completable {
        return getUser().flatMapCompletable { user ->
            Completable.create { emitter ->
                api.unsubscribeFromUserRecommendations(state.token, user.login
                        ?: throw Exception("user login is null"))
                        .enqueue(CompletableCallbackAdapter(emitter))
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun block(): Completable {
        return getUser().flatMapCompletable { user ->
            Completable.create { emitter ->
                api.blockUser(state.token, user.login ?: throw Exception("user login is null"))
                        .enqueue(CompletableCallbackAdapter(emitter))
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun unblock(): Completable {
        return getUser().flatMapCompletable { user ->
            Completable.create { emitter ->
                api.unblockUser(state.token, user.login ?: throw Exception("user login is null"))
                        .enqueue(CompletableCallbackAdapter(emitter))
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchUserAndPosts() = userRepo.fetchUser(userId).flatMap { userPostRepo.fetch() }
            .observeOn(AndroidSchedulers.mainThread())

    fun getUser() = userRepo.getItem(userId).observeOn(AndroidSchedulers.mainThread())

    fun getPosts() = userPostRepo.getAll().observeOn(AndroidSchedulers.mainThread())
}