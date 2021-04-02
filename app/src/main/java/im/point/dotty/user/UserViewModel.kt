/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
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
        with(api.subscribeToUser(state.token, getUser().first().login
                ?: throw Exception("user not found"))) {
            checkSuccessful()
            return@async this
        }
    }

    fun unsubscribe() = viewModelScope.async(Dispatchers.IO) {
        with(api.unsubscribeFromUser(state.token, getUser().first().login
                ?: throw Exception("user not found"))) {
            checkSuccessful()
            return@async this
        }
    }

    fun subscribeRecommendations() = viewModelScope.async(Dispatchers.IO) {
        with(api.subscribeToUserRecommendations(state.token, getUser().first().login
                ?: throw Exception("user not found"))) {
            checkSuccessful()
            return@async this
        }
    }

    fun unsubscribeRecommendations() = viewModelScope.async(Dispatchers.IO) {
        with(api.unsubscribeFromUserRecommendations(state.token, getUser().first().login
                ?: throw Exception("user not found"))) {
            checkSuccessful()
            return@async this
        }
    }

    fun block() = viewModelScope.async(Dispatchers.IO) {
        with(api.blockUser(state.token, getUser().first().login
                ?: throw Exception("user not found"))) {
            checkSuccessful()
            return@async this
        }
    }

    fun unblock() = viewModelScope.async(Dispatchers.IO) {
        with(api.unblockUser(state.token, getUser().first().login
                ?: throw Exception("user not found"))) {
            checkSuccessful()
            return@async this
        }
    }

    fun fetchUserAndPosts() = userRepo.fetchUser(userId)
            .flatMapConcat { userPostRepo.fetch() }
            .flowOn(Dispatchers.IO)

    fun getUser() = userRepo.getItem(userId).flowOn(Dispatchers.IO)

    fun getPosts() = userPostRepo.getAll().flowOn(Dispatchers.IO)
}