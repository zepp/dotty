/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.*

class AvaRepository(private val client: OkHttpClient,
                    private val path: File) {
    private val nameRegex = Regex("(.*):(\\d+)\\.jpg$");
    private val map24 = mutableMapOf<String, Flow<Bitmap>>()
    private val map40 = mutableMapOf<String, Flow<Bitmap>>()
    private val map80 = mutableMapOf<String, Flow<Bitmap>>()
    private val map280 = mutableMapOf<String, Flow<Bitmap>>()

    fun getAvatar(name: String, size: Size): Flow<Bitmap> {
        return map(size).getOrPut(name, {
            fetchAvatar(name, size)
                .flowOn(Dispatchers.IO)
                .stateIn(GlobalScope, SharingStarted.Eagerly,
                    Bitmap.createBitmap(size.dim, size.dim, Bitmap.Config.ARGB_8888))})
    }

    private fun map(size: Size): MutableMap<String, Flow<Bitmap>> = when (size) {
        Size.SIZE_24 -> map24
        Size.SIZE_40 -> map40
        Size.SIZE_80 -> map80
        Size.SIZE_280 -> map280
    }

    private fun fetchAvatar(name: String, size: Size) = flow {
        with(File(path, "$name:${size.dim}.jpg")) {
            val response = client.newCall(getRequest(name, size)).execute()
            if (response.isSuccessful) {
                delete()
                val bytes = response.body()?.bytes() ?: throw Exception("empty response body")
                writeBytes(bytes)
                emit(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ?: throw Exception("failed to decode file"))
            } else {
                throw Exception(response.message())
            }
        }
    }

    private fun getRequest(name: String, size: Size): Request {
        return Request.Builder().url("https://point.im/avatar/$name/${size.dim}")
                .build()
    }

    init {
        path.mkdir()
        GlobalScope.launch {
            path.listFiles()?.forEach {
                nameRegex.matchEntire(it.name)?.groups?.apply {
                    val name = get(1)?.value ?: throw Exception("avatar name is not specified")
                    val size = get(2)?.value?.toInt()
                            ?: throw Exception("avatar size is not specified")
                    map(Size.fromDim(size))[name] = flowOf(BitmapFactory.decodeFile(it.absolutePath))
                }
            }
        }
    }
}

enum class Size(val dim: Int) {
    SIZE_24(24),
    SIZE_40(40),
    SIZE_80(80),
    SIZE_280(280);

    companion object {
        fun fromDim(dim: Int): Size {
            return when (dim) {
                SIZE_24.dim -> SIZE_24
                SIZE_40.dim -> SIZE_40
                SIZE_80.dim -> SIZE_80
                SIZE_280.dim -> SIZE_280
                else -> throw Exception("unknown avatar size")
            }
        }
    }
}