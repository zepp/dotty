/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.domain

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.db.*
import im.point.dotty.login.LoginActivity
import im.point.dotty.main.MainActivity
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.LoginReply
import im.point.dotty.network.LogoutReply
import im.point.dotty.network.SingleCallbackAdapter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class AuthViewModel internal constructor(application: DottyApplication) : AndroidViewModel(application) {
    private val state: AppState
    private val api: AuthAPI
    private val allPostDao: AllPostDao
    private val recentPostDao: RecentPostDao
    private val commentedPostDao: CommentedPostDao
    private val commentDao: CommentDao
    private val userDao: UserDao
    private lateinit var isLoginEnabledEmitter: ObservableEmitter<Boolean>

    val isLoginEnabled: Observable<Boolean> =
            Observable.create<Boolean> { emitter ->
                isLoginEnabledEmitter = emitter
                emitter.onNext(false)
            }
                    .distinctUntilChanged()
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .publish()
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

    fun login(): Single<LoginReply> {
        return Single.create { emitter: SingleEmitter<LoginReply> ->
            isLoginEnabledEmitter.onNext(false)
            api.login(login, password).enqueue(SingleCallbackAdapter(emitter))
        }
                .doOnEvent { _, _ -> updateLoginEnabled() }
                .doAfterSuccess { reply ->
                    state.isLoggedIn = true
                    state.userLogin = login
                    state.csrfToken = reply.csrfToken ?: throw Exception("CSRF token is empty")
                    state.token = reply.token ?: throw Exception("token is empty")
                    resetActivityBackStack()
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun logout(): Single<LogoutReply> {
        return Single.create { emitter: SingleEmitter<LogoutReply> ->
            api.logout(state.token ?: throw Exception("invalid token"),
                    state.csrfToken ?: throw Exception("invalid CSRF token"))
                    .enqueue(SingleCallbackAdapter(emitter))
        }
                .doFinally {
                    state.isLoggedIn = false
                    state.csrfToken = null
                    state.token = null
                    allPostDao.deleteAll()
                    recentPostDao.deleteAll()
                    commentedPostDao.deleteAll()
                    commentDao.deleteAll()
                    userDao.deleteAll()
                    resetActivityBackStack()
                }.observeOn(AndroidSchedulers.mainThread())
    }

    fun resetActivityBackStack() {
        val intent: Intent = if (state.isLoggedIn == true) {
            MainActivity.getIntent(getApplication<Application>().baseContext)
        } else {
            LoginActivity.getIntent(getApplication<Application>().baseContext)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        getApplication<Application>().startActivity(intent)
    }

    init {
        api = application.authApi
        state = application.state
        allPostDao = application.database.getAllPostDao()
        recentPostDao = application.database.getRecentPostDao()
        commentedPostDao = application.database.getCommentedPostDao()
        commentDao = application.database.getCommentDao()
        userDao = application.database.getUserDao()
        isLoginEnabled.subscribe()
        login = state.userLogin ?: ""
    }
}