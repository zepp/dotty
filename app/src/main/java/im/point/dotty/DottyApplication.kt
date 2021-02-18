package im.point.dotty

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.google.gson.GsonBuilder
import im.point.dotty.db.DottyDatabase
import im.point.dotty.domain.AppState
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.RepoFactory
import io.reactivex.plugins.RxJavaPlugins
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DottyApplication : Application() {
    private val executor: ExecutorService = Executors.newCachedThreadPool()
    private val tag = this::class.simpleName
    private val gson = GsonBuilder().setLenient().create()
    private val BASE = "https://point.im"
    private val retrofit = Retrofit.Builder().baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .callbackExecutor(executor)
            .build()

    val database by lazy {
        Room.databaseBuilder(applicationContext, DottyDatabase::class.java, "main")
                .setQueryExecutor(executor)
                .setTransactionExecutor(executor)
                .build()
    }

    val state by lazy {
        AppState(applicationContext)
    }

    val mainApi by lazy {
        retrofit.create(PointAPI::class.java)
    }

    val authApi by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val repoFactory by lazy {
        RepoFactory(mainApi, database, state)
    }

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler{error -> Log.e(tag, "RxError:", error)}
    }
}