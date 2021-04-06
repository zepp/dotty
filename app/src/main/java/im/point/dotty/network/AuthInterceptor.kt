package im.point.dotty.network

import im.point.dotty.BuildConfig
import im.point.dotty.common.AppState
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val state: AppState) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
                .addHeader("User-Agent", "Dotty/" + BuildConfig.VERSION_CODE)
                .addHeader("Authorization", state.token)
                .addHeader("X-CSRF", state.csrfToken)
                .build()
        return chain.proceed(newRequest)
    }
}