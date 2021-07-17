/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import im.point.dotty.common.digest
import im.point.dotty.db.PostFileDao
import im.point.dotty.model.PostFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.Executors

class PostFileRepository(private val client: OkHttpClient,
                         private val dao: PostFileDao,
                         private val root: File) {
    // standalone dispatcher to work with cache map since getOrPut is not atomic
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val sha1 = MessageDigest.getInstance("SHA-1")
    private val cache = mutableMapOf<String, Entry>()

    fun getPostFiles(postId: String) = dao.getPostFiles(postId)
            .transform { files ->
                with(mutableListOf<Bitmap>()) {
                    files.toBitmapFlow().toList(this)
                    emit(toList())
                }
            }
            .catch { Log.e(PostFileRepository::class.simpleName, "image fetch error: ", it) }

    private fun List<PostFile>.toBitmapFlow() = flow {
        map { file ->
            GlobalScope.async(dispatcher) {
                cache.getOrPut(file.url.digest(sha1), { Entry(file.loadOrFetch()) }).data()
            }
        }.forEach { emit(it.await()) }
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
        root.deleteRecursively()
    }

    suspend fun cleanupAllMemCache() = withContext(dispatcher) {
        cache.clear()
    }

    private suspend fun cleanupStaleMemCache() = withContext(dispatcher) {
        cache.onEach { it.value.tick() }
        val stale = cache.filter { it.value.isStale }
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
        fun data(): Bitmap {
            lifetime++
            return bitmap
        }

        fun tick() = lifetime--

        val isStale: Boolean
            get() = lifetime <= 0
    }

    companion object {
        const val delaySec = 30L
    }
}