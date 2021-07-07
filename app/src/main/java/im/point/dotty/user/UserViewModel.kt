/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import android.util.Log
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.model.User
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FlowPreview
class UserViewModel(application: DottyApplication, vararg args: Any) : DottyViewModel(application) {
    private val userId = args[0] as Long
    private val userLogin = args[1] as String

    private val userRepo = application.repoFactory.getUserRepo()
    private val userPostRepo = application.repoFactory.getUserPostRepo(userId)
    private val api: PointAPI = application.mainApi
    private val avaRepo = application.avaRepo

    // fetch data first or getItem and fetchAll throw exception
    private val fetched = userRepo.fetchUser(userId)
            .catch { Log.e(this@UserViewModel::class.simpleName, "error: ", it) }
            .flowOn(Dispatchers.IO)

    val isActionsVisible = MutableStateFlow(state.id != userId)
    val user = fetched.flatMapConcat { userRepo.getItem(userId) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, User(userId, userLogin))
    val posts = fetched.flatMapConcat { userPostRepo.getAll() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    // initially all switches are turned off
    val isSubscribed = user.map { it.subscribed == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isRecSubscribed = user.map { it.recSubscribed == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isBlocked = user.map { it.blocked == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val login = user.map { it.login }
        .stateIn(viewModelScope, SharingStarted.Eagerly, userLogin)

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            launch { fetched.flatMapConcat { userPostRepo.fetchAll() }.collect() }
        }
    }

    fun onSubscribeChecked(value: Boolean) = flowOf(value)
            .filter { it.xor(isSubscribed.value) }
            .map { if (value) subscribe() else unsubscribe() }
            .flatMapConcat { userRepo.fetchUser(userId) }
            .catch { logAndRethrow(it) }
            .flowOn(Dispatchers.IO)

    fun onRecSubscribeChecked(value: Boolean) = flowOf(value)
            .filter { it.xor(isRecSubscribed.value) }
            .map { if (value) subscribeRecommendations() else unsubscribeRecommendations() }
            .flatMapConcat { userRepo.fetchUser(userId) }
            .catch { logAndRethrow(it) }
            .flowOn(Dispatchers.IO)

    fun onBlockChecked(value: Boolean) = flowOf(value)
            .filter { it.xor(isBlocked.value) }
            .map { if (value) block() else unblock() }
            .flatMapConcat { userRepo.fetchUser(userId) }
            .catch { logAndRethrow(it) }
            .flowOn(Dispatchers.IO)

    fun getUserAvatar() = avaRepo.getAvatar(login.value, Size.SIZE_280)

    fun getPostAvatar(login: String) = avaRepo.getAvatar(login, Size.SIZE_80)

    private fun logAndRethrow(e: Throwable) {
        Log.e(this::class.simpleName, "error: ", e)
        throw e
    }

    private suspend fun subscribe() = withContext(Dispatchers.IO) {
        api.subscribeToUser(login.value).apply {
            checkSuccessful()
            Log.d(this::class.simpleName, "subscribed to a user")
        }
    }

    private suspend fun unsubscribe() = withContext(Dispatchers.IO) {
        api.unsubscribeFromUser(login.value).apply {
            checkSuccessful()
            Log.d(this::class.simpleName, "unsubscribed from a user")
        }
    }

    private suspend fun subscribeRecommendations() = withContext(Dispatchers.IO) {
        api.subscribeToUserRecommendations(login.value).apply {
            checkSuccessful()
            Log.d(this::class.simpleName, "subscribed to recommendations")
        }
    }

    private suspend fun unsubscribeRecommendations() = withContext(Dispatchers.IO) {
        api.unsubscribeFromUserRecommendations(login.value).apply {
            checkSuccessful()
            Log.d(this::class.simpleName, "unsubscribed from recommendations")
        }
    }

    private suspend fun block() = withContext(Dispatchers.IO) {
        api.blockUser(login.value).apply {
            checkSuccessful()
            Log.d(this::class.simpleName, "user is blocked")
        }
    }

    private suspend fun unblock() = withContext(Dispatchers.IO) {
        api.unblockUser(login.value).apply {
            checkSuccessful()
            Log.d(this::class.simpleName, "user is unblocked")
        }
    }

    fun fetchUserAndPosts() = userRepo.fetchUser(userId)
            .flatMapConcat { userPostRepo.fetchAll() }
            .flowOn(Dispatchers.IO)

    fun fetchBefore() = fetched.flatMapConcat { userPostRepo.fetchBefore() }
            .flowOn(Dispatchers.IO)
}