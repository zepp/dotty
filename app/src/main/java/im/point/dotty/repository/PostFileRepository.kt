/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import im.point.dotty.common.asFlow
import im.point.dotty.common.toListFlow
import im.point.dotty.db.PostFileDao
import im.point.dotty.model.PostFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.Executors

class PostFileRepository(
        private val client: OkHttpClient,
        private val dao: PostFileDao,
        private val root: File
) {
    // standalone dispatcher to work with cache map since getOrPut is not atomic
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val cache = mutableMapOf<String, Entry>()

    fun getPostFiles(postId: String) = dao.getPostFiles(postId)
            .flatMapConcat {
                it.asFlow()
                        .flatMapMerge { file -> getFlowFromCache(file) }
                        .toListFlow()
                        .catch { e ->
                            Log.w(PostFileRepository::class.simpleName, "failed to load/fetch image: ", e)
                        }
            }

    private suspend fun getFlowFromCache(file: PostFile) = withContext(dispatcher) {
        cache.getOrPut(file.id, { Entry(file.loadOrFetch().flowOn(Dispatchers.IO).single()) }).flow()
    }

    private fun PostFile.loadOrFetch(): Flow<Bitmap> {
        with(File(root, id)) {
            return if (exists()) {
                flow { emit(BitmapFactory.decodeFile(absolutePath)) }
            } else {
                client.newCall(getRequest(url)).asFlow().map { response ->
                    if (response.isSuccessful) {
                        root.mkdir()
                        response.body()?.bytes()?.let {
                            writeBytes(it)
                            BitmapFactory.decodeByteArray(it, 0, it.size)
                                    ?: throw Exception("failed to decode image")
                        }
                                ?: throw Exception("empty response body")
                    } else {
                        throw Exception(response.message())
                    }
                }
            }
        }
    }

    private fun getRequest(url: String) = Request.Builder()
            .url(url)
            .addHeader("accept", "image/*")
            .build()

    suspend fun cleanupFileCache() = withContext(dispatcher) {
        cleanupAllMemCache()
        root.deleteRecursively()
        root.mkdir()
    }

    suspend fun cleanupAllMemCache() = withContext(dispatcher) {
        cache.clear()
    }

    private suspend fun cleanupStaleMemCache() = withContext(dispatcher) {
        val stale = cache.filter { it.value.apply { tick() }.isStale }
        stale.forEach {
            Log.d(PostFileRepository::class.simpleName, "cache entry is released: ${it.value}")
            cache.remove(it.key)
        }
    }

    init {
        GlobalScope.launch(dispatcher) {
            do {
                cleanupStaleMemCache()
                delay(1000 * delaySec)
            } while (isActive)
        }
    }

    internal data class Entry(val bitmap: Bitmap, var lifetime: Int = 0) {
        private val flow_ = flowOf(bitmap)

        fun flow(): Flow<Bitmap> {
            lifetime++
            return flow_
        }

        fun tick() = lifetime--

        val isStale: Boolean
            get() = lifetime <= 0
    }

    companion object {
        const val delaySec = 30L
    }
}