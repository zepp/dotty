/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.common.Shared
import im.point.dotty.network.PointAPI
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter

class UserViewModel(application: DottyApplication, private val userId: Long) : AndroidViewModel(application) {
    private val userRepo = application.repoFactory.getUserRepo()
    private val userPostRepo = application.repoFactory.getUserPostRepo(userId)
    private val api: PointAPI = application.mainApi
    private val shared: Shared = Shared(application.baseContext, application.state, application.mainApi)
    private lateinit var actionsEmitter: SingleEmitter<Boolean>

    val isActionsVisible: Single<Boolean> = Single.create { emitter -> actionsEmitter = emitter }

    fun subscribe(): Completable {
        return Completable.complete()
    }

    fun unsubscribe(): Completable {
        return Completable.complete()
    }

    fun subscribeRecommendations(): Completable {
        return Completable.complete()
    }

    fun unsubscribeRecommendations(): Completable {
        return Completable.complete()
    }

    fun block(): Completable {
        return Completable.complete()
    }

    fun unblock(): Completable {
        return Completable.complete()
    }

    fun getUser() = userRepo.getItem(userId!!)
}