/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import im.point.dotty.db.PostFileDao
import im.point.dotty.model.PostFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class PostFileRepository(private val client: OkHttpClient,
                         private val dao: PostFileDao,
                         private val root: File) {

    fun getPostFiles(postId: String): Flow<List<Bitmap>> {
        return dao.getPostFiles(postId).map {
            it.map { file ->
                with(File(root, "${file.id}.jpg")) {
                    if (exists()) {
                        BitmapFactory.decodeFile(absolutePath)
                    } else {
                        fetchFile(file, this)
                    }
                }
            }
        }
    }

    private suspend fun fetchFile(file: PostFile, path: File) = withContext(Dispatchers.IO) {
        root.mkdir()
        val response = client.newCall(getRequest(file.url)).execute()
        if (response.isSuccessful) {
            val bytes = response.body()?.bytes() ?: throw Exception("empty response body")
            path.writeBytes(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ?: throw Exception("failed to decode file")
        } else {
            throw Exception(response.message())
        }
    }

    private fun getRequest(url: String) = Request.Builder().url(url).build()

    suspend fun cleanup() = withContext(Dispatchers.IO) {
        root.deleteRecursively()
    }
}