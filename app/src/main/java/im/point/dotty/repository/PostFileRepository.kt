/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
        cache.getOrPut(file.id, { Entry(file.loadOrFetch()) }).flow()
    }

    private suspend fun PostFile.loadOrFetch() = withContext(Dispatchers.IO) {
        with(File(root, id)) {
            if (exists()) {
                BitmapFactory.decodeFile(absolutePath)
            } else {
                val response = client.newCall(getRequest(url)).execute()
                if (response.isSuccessful) {
                    val bytes = response.body()?.bytes() ?: throw Exception("empty response body")
                    root.mkdir()
                    writeBytes(bytes)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ?: throw Exception("failed to decode file")
                } else {
                    throw Exception(response.message())
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