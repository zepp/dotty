/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import android.graphics.BitmapFactory
import android.util.Log
import im.point.dotty.db.PostFileDao
import im.point.dotty.model.PostFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class PostFileRepository(private val client: OkHttpClient,
                         private val dao: PostFileDao,
                         private val root: File) {

    fun getPostFiles(postId: String) = dao.getPostFiles(postId)
        .map { it.toBitmapList() }
        .catch { Log.e(this::class.simpleName, "image fetch error: ", it) }

    private suspend fun List<PostFile>.toBitmapList() = map { file ->
        with(File(root, file.id)) {
            if (exists()) {
                BitmapFactory.decodeFile(absolutePath)
            } else {
                file.fetch(this)
            }
        }
    }

    private suspend fun PostFile.fetch(to: File) = withContext(Dispatchers.IO) {
        root.mkdir()
        val response = client.newCall(getRequest(url)).execute()
        if (response.isSuccessful) {
            val bytes = response.body()?.bytes() ?: throw Exception("empty response body")
            to.writeBytes(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: throw Exception("failed to decode file")
        } else {
            throw Exception(response.message())
        }
    }

    private fun getRequest(url: String) = Request.Builder()
        .url(url)
        .addHeader("accept", "image/*")
        .build()

    suspend fun cleanup() = withContext(Dispatchers.IO) {
        root.deleteRecursively()
    }
}