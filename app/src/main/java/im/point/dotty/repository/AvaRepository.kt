/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class AvaRepository(private val client: OkHttpClient,
                    private val path: File) {
    private val map24 = mutableMapOf<String, Flow<Bitmap>>()
    private val map40 = mutableMapOf<String, Flow<Bitmap>>()
    private val map80 = mutableMapOf<String, Flow<Bitmap>>()
    private val map280 = mutableMapOf<String, Flow<Bitmap>>()

    fun getAvatar(name: String, size: Size): Flow<Bitmap> {
        return map(size).getOrPut(name, {
            fetchOrLoad(name, size)
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    Log.e(AvaRepository::class.simpleName, "failed to fetch/load avatar", e)
                }
                .stateIn(
                    GlobalScope, SharingStarted.Eagerly,
                    Bitmap.createBitmap(size.dim, size.dim, Bitmap.Config.ARGB_8888)
                )
        })
    }

    suspend fun cleanupMemoryCache() = withContext(Dispatchers.Main) {
        map24.clear()
        map40.clear()
        map80.clear()
        map280.clear()
    }

    private fun map(size: Size): MutableMap<String, Flow<Bitmap>> = when (size) {
        Size.SIZE_24 -> map24
        Size.SIZE_40 -> map40
        Size.SIZE_80 -> map80
        Size.SIZE_280 -> map280
    }

    private fun root(size: Size) = File(path, size.dim.toString())

    private fun fetchOrLoad(name: String, size: Size) = flow {
        val root = root(size)
        root.mkdir()
        with(File(root, "$name.jpg")) {
            if (exists()) {
                emit(BitmapFactory.decodeFile(absolutePath))
            } else {
                val response = client.newCall(getRequest(name, size)).execute()
                if (response.isSuccessful) {
                    delete()
                    val bytes = response.body()?.bytes() ?: throw Exception("empty response body")
                    writeBytes(bytes)
                    emit(
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            ?: throw Exception("failed to decode file")
                    )
                } else {
                    throw Exception(response.message())
                }
            }
        }
    }

    private fun getRequest(name: String, size: Size) =
        Request.Builder().url("https://point.im/avatar/$name/${size.dim}")
            .build()

    suspend fun cleanupFileCache() = withContext(Dispatchers.IO) {
        path.deleteRecursively()
    }

    init {
        path.mkdir()
    }
}

enum class Size(val dim: Int) {
    SIZE_24(24),
    SIZE_40(40),
    SIZE_80(80),
    SIZE_280(280);
}