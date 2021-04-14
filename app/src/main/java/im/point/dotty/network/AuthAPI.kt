/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthAPI {
    @FormUrlEncoded
    @POST("/api/login")
    suspend fun login(@Field("login") login: String, @Field("password") password: String): LoginReply

    @FormUrlEncoded
    @POST("/api/logout")
    suspend fun logout(@Header("Authorization") token: String, @Field("csrf_token") csrfToken: String): LogoutReply
}