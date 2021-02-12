package im.point.dotty.domain

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import im.point.dotty.login.LoginActivity
import im.point.dotty.main.MainActivity
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.LoginReply
import im.point.dotty.network.LogoutReply
import im.point.dotty.network.SingleCallbackAdapter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("CheckResult")
class AuthViewModel internal constructor(application: Application) : AndroidViewModel(application) {
    private val state: AppState
    private val gson: Gson
    private val retrofit: Retrofit
    private val api: AuthAPI

    fun login(name: String, password: String): Single<LoginReply> {
        val single = Single.create { emitter: SingleEmitter<LoginReply> -> api.login(name, password).enqueue(SingleCallbackAdapter(emitter)) }
        single.subscribe { value -> state.isLoggedIn = true
            state.userName = name
            state.csrfToken = value.csrfToken ?: throw Exception("CSRF token is empty")
            state.token = value.token ?: throw Exception("token is empty")}
        return single.observeOn(AndroidSchedulers.mainThread())
    }

    fun logout(): Single<LogoutReply> {
        val single = Single.create { emitter: SingleEmitter<LogoutReply> ->
            api.logout(state.csrfToken ?: throw Exception("invalid CSRF token")).enqueue(SingleCallbackAdapter(emitter)) }
        single.subscribe(object  : DisposableSingleObserver<LogoutReply>() {
            override fun onSuccess(logoutReply: LogoutReply) {
                state.isLoggedIn = false
                state.csrfToken = null
                state.token = null
            }

            override fun onError(e: Throwable) {}
        })
        return single.observeOn(AndroidSchedulers.mainThread())
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

    companion object {
        private const val BASE = "https://point.im"
    }

    init {
        gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        api = retrofit.create(AuthAPI::class.java)
        state = AppState.getInstance(application)
    }
}