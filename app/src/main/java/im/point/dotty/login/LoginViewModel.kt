/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.login

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@SuppressLint("CheckResult")
class LoginViewModel internal constructor(application: DottyApplication) : DottyViewModel(application) {
    private val userRepo: UserRepo = UserRepo(application.mainApi, state, application.database.getUserDao())

    val isLoginEnabled = Channel<Boolean>(Channel.CONFLATED)

    var login: String = ""
        set(value) {
            field = value
            viewModelScope.launch {
                updateLoginEnabled()
            }
        }

    var password: String = ""
        set(value) {
            field = value
            viewModelScope.launch {
                updateLoginEnabled()
            }
        }

    private suspend fun updateLoginEnabled() {
        isLoginEnabled.send(!(login.trim().isBlank() || password.isBlank()))
    }

    fun login() = flow {
        try {
            with(authAPI.login(login, password)) {
                checkSuccessful()
                state.isLoggedIn = true
                state.userLogin = login
                state.csrfToken = csrfToken
                        ?: throw Exception("CSRF token is empty")
                state.token = token ?: throw Exception("token is empty")
                resetActivityBackStack()
                userRepo.fetchMe()
                emit(this)
            }
        } finally {
            updateLoginEnabled()
        }
    }.flowOn(Dispatchers.IO)

    init {
        login = state.userLogin ?: ""
    }
}