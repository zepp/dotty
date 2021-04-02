/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.*

class AvaRepository(private val client: OkHttpClient,
                    private val path: File) {
    private val nameRegex = Regex("(.*):(\\d+)\\.jpg$");
    private val map40 = Collections.synchronizedMap(mutableMapOf<String, Bitmap>())
    private val map80 = Collections.synchronizedMap(mutableMapOf<String, Bitmap>())
    private val map280 = Collections.synchronizedMap(mutableMapOf<String, Bitmap>())

    fun getAvatar(name: String, size: Size): Flow<Bitmap> {
        with(when (size) {
            Size.SIZE_40 -> map40.get(name)
            Size.SIZE_80 -> map80.get(name)
            Size.SIZE_280 -> map280.get(name)
            else -> null
        })
        {
            return if (this == null) {
                fetchAvatar(name, size).flowOn(Dispatchers.IO)
            } else {
                flowOf(this)
            }
        }
    }

    fun fetchAvatar(name: String, size: Size) = flow {
        with(File(path, "$name:${size.dim}.jpg")) {
            val response = client.newCall(getRequest(name, size)).execute()
            if (response.isSuccessful) {
                delete()
                val bytes = response.body()?.bytes() ?: throw Exception("empty response body")
                writeBytes(bytes)
                with(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)) {
                    if (this == null) {
                        throw Exception("failed to decode file")
                    }
                    when (size) {
                        Size.SIZE_40 -> map40[name] = this
                        Size.SIZE_80 -> map80[name] = this
                        Size.SIZE_280 -> map280[name] = this
                    }
                    emit(this)
                }
            } else {
                throw Exception(response.message())
            }
        }
    }

    private fun getRequest(name: String, size: Size): Request {
        return Request.Builder().url("https://i.point.im/a/${size.dim}/$name.jpg")
                .build()
    }

    init {
        path.mkdir()
        GlobalScope.launch {
            path.listFiles()?.forEach {
                nameRegex.matchEntire(it.name)?.groups?.apply {
                    val name = get(1)?.value
                    val size = get(2)?.value?.toInt()
                            ?: throw Exception("avatar size is not specifid")
                    val bitmap = BitmapFactory.decodeFile(it.absolutePath)
                    when (Size.fromDim(size)) {
                        Size.SIZE_40 -> map40[name] = bitmap
                        Size.SIZE_80 -> map80[name] = bitmap
                        Size.SIZE_280 -> map280[name] = bitmap
                    }
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
                else -> throw Exception("unknown dimensin")
            }
        }
    }
}