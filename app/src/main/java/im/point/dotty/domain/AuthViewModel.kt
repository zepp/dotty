package im.point.dotty.domain

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.db.AllPostDao
import im.point.dotty.db.CommentDao
import im.point.dotty.db.CommentedPostDao
import im.point.dotty.db.RecentPostDao
import im.point.dotty.login.LoginActivity
import im.point.dotty.main.MainActivity
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.LoginReply
import im.point.dotty.network.LogoutReply
import im.point.dotty.network.SingleCallbackAdapter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class AuthViewModel internal constructor(application: DottyApplication) : AndroidViewModel(application) {
    private val state: AppState
    private val api: AuthAPI
    private val allPostDao: AllPostDao
    private val recentPostDao: RecentPostDao
    private val commentedPostDao: CommentedPostDao
    private val commentDao: CommentDao

    fun login(name: String, password: String): Single<LoginReply> {
        return Single.create { emitter: SingleEmitter<LoginReply> ->
            api.login(name, password).enqueue(SingleCallbackAdapter(emitter))
        }
                .observeOn(Schedulers.io())
                .doAfterSuccess { reply ->
                    state.isLoggedIn = true
                    state.userLogin = name
                    state.csrfToken = reply.csrfToken ?: throw Exception("CSRF token is empty")
                    state.token = reply.token ?: throw Exception("token is empty")
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun logout(): Single<LogoutReply> {
        return Single.create { emitter: SingleEmitter<LogoutReply> ->
            api.logout(state.token ?: throw Exception("invalid token"),
                    state.csrfToken ?: throw Exception("invalid CSRF token"))
                    .enqueue(SingleCallbackAdapter(emitter))
        }
                .observeOn(Schedulers.io())
                .doFinally {
                    state.isLoggedIn = false
                    state.csrfToken = null
                    state.token = null
                    allPostDao.deleteAll()
                    recentPostDao.deleteAll()
                    commentedPostDao.deleteAll()
                    commentDao.deleteAll()
                }
                .observeOn(AndroidSchedulers.mainThread())
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
    }
}