/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.network.Envelope
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn

class UserViewModel(application: DottyApplication, private val userId: Long) : AndroidViewModel(application) {
    private val userRepo = application.repoFactory.getUserRepo()
    private val userPostRepo = application.repoFactory.getUserPostRepo(userId)
    private val api: PointAPI = application.mainApi
    private val state: AppState = application.state
    private val avaRepo = application.avaRepo

    val isActionsVisible = viewModelScope.produce {
        send(state.id != userId)
    }

    fun getAvatar(name: String) = avaRepo.getAvatar(name, Size.SIZE_80)

    fun subscribe() = viewModelScope.async(Dispatchers.IO) {
        val user = getUser().first()
        if (user.subscribed == false) {
            with(api.subscribeToUser(user.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unsubscribe() = viewModelScope.async(Dispatchers.IO) {
        val user = getUser().first()
        if (user.subscribed == true) {
            with(api.unsubscribeFromUser(user.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun subscribeRecommendations() = viewModelScope.async(Dispatchers.IO) {
        val user = getUser().first()
        if (user.recSubscribed == false) {
            with(api.subscribeToUserRecommendations(user.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unsubscribeRecommendations() = viewModelScope.async(Dispatchers.IO) {
        val user = getUser().first()
        if (user.recSubscribed == true) {
            with(api.unsubscribeFromUserRecommendations(user.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun block() = viewModelScope.async(Dispatchers.IO) {
        val user = getUser().first()
        if (user.blocked == false) {
            with(api.blockUser(user.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unblock() = viewModelScope.async(Dispatchers.IO) {
        val user = getUser().first()
        if (user.blocked == true) {
            with(api.unblockUser(user.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun fetchUserAndPosts() = userRepo.fetchUser(userId)
            .flatMapConcat { userPostRepo.fetchAll() }
            .flowOn(Dispatchers.IO)

    fun getUser() = userRepo.getItem(userId).flowOn(Dispatchers.IO)

    fun getPosts() = userPostRepo.getAll().flowOn(Dispatchers.IO)
}