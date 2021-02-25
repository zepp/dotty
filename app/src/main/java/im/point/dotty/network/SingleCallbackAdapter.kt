/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import io.reactivex.SingleEmitter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleCallbackAdapter<T : Envelope>(private val emitter: SingleEmitter<T>) : Callback<T?> {
    override fun onResponse(call: Call<T?>, response: Response<T?>) {
        if (!response.isSuccessful) {
            emitter.onError(RuntimeException("request failed with error code: " + response.code()))
        }
        val contentType = response.headers()["content-type"]
        if (contentType != "application/json") {
            emitter.onError(RuntimeException("content type is unsupported: $contentType"))
            return
        }
        val envelope = response.body()
        if (envelope == null) {
            emitter.onError(RuntimeException("empty body"))
        } else{
            if (envelope.error == null) {
                emitter.onSuccess(envelope)
            } else {
                emitter.onError(RuntimeException(envelope.error))
            }
        }
    }

    override fun onFailure(call: Call<T?>, t: Throwable) {
        emitter.onError(t)
    }
}