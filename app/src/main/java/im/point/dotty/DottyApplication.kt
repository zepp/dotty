/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.AuthInterceptor
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.AvaRepository
import im.point.dotty.repository.PostFileRepository
import im.point.dotty.repository.RepoFactory
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.Executors

class DottyApplication : Application() {
    private val executor = Executors.newCachedThreadPool()
    private val gson = GsonBuilder().setLenient().create()
    private val client = OkHttpClient.Builder()
            .dispatcher(Dispatcher(executor))
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
        val client = OkHttpClient.Builder()
                .dispatcher(Dispatcher(executor))
                .addInterceptor(AuthInterceptor(state))
                .build()
        with(Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executor)
                .client(client)
                .build()) {
            create(PointAPI::class.java)
        }

    }

    val authApi by lazy {
        with(Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executor)
                .client(client)
                .build()) {
            create(AuthAPI::class.java)
        }
    }

    val repoFactory by lazy {
        RepoFactory(mainApi, database, state)
    }

    val avaRepo by lazy {
        AvaRepository(client, File(applicationContext.externalCacheDir, "ava"))
    }

    val postFilesRepo by lazy {
        PostFileRepository(client, database.getPostFileDao(), File(applicationContext.externalCacheDir, "files"))
    }

    companion object {
        const val BASE = "https://point.im"
    }
}