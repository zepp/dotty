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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("CheckResult")
class LoginViewModel internal constructor(application: DottyApplication) : DottyViewModel(application) {
    private val userRepo: UserRepo = UserRepo(application.mainApi, state, application.database.getUserDao())

    val isLoginEnabled = MutableStateFlow(false)

    val login = MutableStateFlow("")

    val password = MutableStateFlow("")

    private suspend fun updateLoginEnabled() {
        isLoginEnabled.emit(!(login.value.isBlank() || password.value.isBlank()))
    }

    fun login() = viewModelScope.async(Dispatchers.IO) {
        val login = login.value
        with(authAPI.login(login, password.value)) {
            checkSuccessful()
            state.isLoggedIn = true
            state.userLogin = login
            state.csrfToken = csrfToken
                    ?: throw Exception("CSRF token is empty")
            state.token = token ?: throw Exception("token is empty")
            resetActivityBackStack()
            userRepo.fetchMe()
            this
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            login.collect { updateLoginEnabled() }
        }
        viewModelScope.launch(Dispatchers.Default) {
            password.collect { updateLoginEnabled() }
        }
    }
}