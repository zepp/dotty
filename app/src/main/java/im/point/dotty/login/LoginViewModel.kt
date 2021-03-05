/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.login

import android.annotation.SuppressLint
import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.common.Shared
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.LoginReply
import im.point.dotty.network.SingleCallbackAdapter
import im.point.dotty.repository.UserRepo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class LoginViewModel internal constructor(application: DottyApplication) : AndroidViewModel(application) {
    private val state: AppState = application.state
    private val api: AuthAPI = application.authApi
    private val shared: Shared = Shared(application.baseContext, application.state, application.mainApi)
    private val userRepo: UserRepo = UserRepo(application.mainApi, state, application.database.getUserDao())
    private lateinit var isLoginEnabledEmitter: ObservableEmitter<Boolean>

    val isLoginEnabled: Observable<Boolean> =
            Observable.create<Boolean> { emitter ->
                isLoginEnabledEmitter = emitter
                emitter.onNext(false)
            }
                    .distinctUntilChanged()
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .replay(1)
                    .autoConnect()
                    .observeOn(AndroidSchedulers.mainThread())

    var login: String = ""
        set(value) {
            field = value.trim()
            updateLoginEnabled()
        }

    var password: String = ""
        set(value) {
            field = value
            updateLoginEnabled()
        }

    private fun updateLoginEnabled() {
        isLoginEnabledEmitter.onNext(!(login.isBlank() || password.isBlank()))
    }

    fun login(): Completable {
        return Completable.fromSingle(
                Single.create<LoginReply> { emitter ->
                    isLoginEnabledEmitter.onNext(false)
                    api.login(login, password).enqueue(SingleCallbackAdapter(emitter))
                }
                        .doOnEvent { _, _ -> updateLoginEnabled() }
                        .doAfterSuccess { reply ->
                            state.isLoggedIn = true
                            state.userLogin = login
                            state.csrfToken = reply.csrfToken
                                    ?: throw Exception("CSRF token is empty")
                            state.token = reply.token ?: throw Exception("token is empty")
                            shared.resetActivityBackStack()
                            userRepo.fetchMe()
                        })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun resetActivityBackStack() {
        shared.resetActivityBackStack()
    }

    init {
        isLoginEnabled.subscribe()
        login = state.userLogin ?: ""
    }
}