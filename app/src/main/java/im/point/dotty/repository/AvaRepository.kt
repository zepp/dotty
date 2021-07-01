/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.*

class AvaRepository(private val client: OkHttpClient,
                    private val path: File) {
    private val nameRegex = Regex("(.*):(\\d+)\\.jpg$");
    private val map24 = Collections.synchronizedMap(mutableMapOf<String, Bitmap>())
    private val map40 = Collections.synchronizedMap(mutableMapOf<String, Bitmap>())
    private val map80 = Collections.synchronizedMap(mutableMapOf<String, Bitmap>())
    private val map280 = Collections.synchronizedMap(mutableMapOf<String, Bitmap>())

    fun getAvatar(name: String, size: Size): Flow<Bitmap> =
            flowOf(map(size)[name]).map { it ?: fetchAvatar(name, size) }.catch {
                Bitmap.createBitmap(size.dim, size.dim, Bitmap.Config.ARGB_8888)
            }

    private fun map(size: Size): MutableMap<String, Bitmap> = when (size) {
        Size.SIZE_24 -> map24
        Size.SIZE_40 -> map40
        Size.SIZE_80 -> map80
        Size.SIZE_280 -> map280
    }

    private suspend fun fetchAvatar(name: String, size: Size) = withContext(Dispatchers.IO) {
        with(File(path, "$name:${size.dim}.jpg")) {
            val response = client.newCall(getRequest(name, size)).execute()
            if (response.isSuccessful) {
                delete()
                val bytes = response.body()?.bytes() ?: throw Exception("empty response body")
                writeBytes(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size).also {
                    if (it == null) {
                        throw Exception("failed to decode file")
                    } else {
                        map(size)[name] = it
                    }
                }
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
                    map(Size.fromDim(size))[name] = BitmapFactory.decodeFile(it.absolutePath)
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