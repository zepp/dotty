/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.model.User
import im.point.dotty.network.Envelope
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(application: DottyApplication, vararg args: Any) : DottyViewModel(application) {
    private val userId = args[0] as Long
    private val userLogin = args[1] as String

    private val userRepo = application.repoFactory.getUserRepo()
    private val userPostRepo = application.repoFactory.getUserPostRepo(userId)
    private val api: PointAPI = application.mainApi
    private val avaRepo = application.avaRepo

    private val onSubscribe_ = MutableSharedFlow<Boolean>()
    private val onRecSubscribe_ = MutableSharedFlow<Boolean>()
    private val onBlock_ = MutableSharedFlow<Boolean>()

    val isActionsVisible = MutableStateFlow(state.id != userId)
    val user = MutableStateFlow(User(userId, userLogin))
    val onSubscribe = onSubscribe_.distinctUntilChanged()
    val onRecSubscribe = onRecSubscribe_.distinctUntilChanged()
    val onBlock = onBlock_.distinctUntilChanged()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.fetchUser(userId).collect {
                launch { userRepo.getItem(userId).collect { user.emit(it) } }
                launch { userPostRepo.fetchAll().collect() }
            }
        }
    }

    fun onSubscribeChecked(value: Boolean) = viewModelScope.launch {
        onSubscribe_.emit(value)
    }

    fun onRecSubscribeChecked(value: Boolean) = viewModelScope.launch {
        onRecSubscribe_.emit(value)
    }

    fun onBlockChecked(value: Boolean) = viewModelScope.launch {
        onBlock_.emit(value)
    }

    fun getAvatar(login: String) = avaRepo.getAvatar(login, Size.SIZE_280)

    fun getCommentAvatar(login: String) = avaRepo.getAvatar(login, Size.SIZE_80)

    fun subscribe() = viewModelScope.async(Dispatchers.IO) {
        val model = user.value
        if (model.subscribed == false) {
            with(api.subscribeToUser(model.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unsubscribe() = viewModelScope.async(Dispatchers.IO) {
        val model = user.value
        if (model.subscribed == true) {
            with(api.unsubscribeFromUser(model.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun subscribeRecommendations() = viewModelScope.async(Dispatchers.IO) {
        val model = user.value
        if (model.recSubscribed == false) {
            with(api.subscribeToUserRecommendations(model.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unsubscribeRecommendations() = viewModelScope.async(Dispatchers.IO) {
        val model = user.value
        if (model.recSubscribed == true) {
            with(api.unsubscribeFromUserRecommendations(model.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun block() = viewModelScope.async(Dispatchers.IO) {
        val model = user.value
        if (model.blocked == false) {
            with(api.blockUser(model.login
                    ?: throw Exception("user not found"))) {
                checkSuccessful()
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unblock() = viewModelScope.async(Dispatchers.IO) {
        val model = user.value
        if (model.blocked == true) {
            with(api.unblockUser(model.login
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

    fun getPosts() = userPostRepo.getAll().flowOn(Dispatchers.IO)
}