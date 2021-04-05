/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import retrofit2.http.*

interface PointAPI {
    @GET("/api/recent")
    suspend fun getRecent(@Header("Authorization") token: String, @Query("before") before: Long?): PostsReply

    @GET("/api/all")
    suspend fun getAll(@Header("Authorization") token: String, @Query("before") before: Long?): PostsReply

    @GET("/api/comments")
    suspend fun getComments(@Header("Authorization") token: String, @Query("before") before: Long?): PostsReply

    @GET("/api/bookmarks")
    suspend fun getBookmarks(@Header("Authorization") token: String, @Query("before") before: Long?): PostsReply

    @GET("/api/tags")
    suspend fun getTagged(@Header("Authorization") token: String, @Query("tag") tag: String, @Query("before") before: Long?): PostsReply

    @GET("/api/tags/{user}")
    suspend fun getUserTagged(@Header("Authorization") token: String, @Path("user") user: String, @Query("tag") tag: String, @Query("before") before: Long?): PostsReply

    @GET("/api/blog/{user}")
    suspend fun getUserPosts(@Header("Authorization") token: String, @Path("user") user: String, @Query("before") before: Long?): PostsReply

    @GET("/api/post/{post}")
    suspend fun getPost(@Header("Authorization") token: String, @Path("post") id: String): PostReply

    @GET("/api/user/login/{login}")
    suspend fun getUser(@Header("Authorization") token: String, @Path("login") login: String): UserReply

    @GET("/api/user/id/{id}")
    suspend fun getUser(@Header("Authorization") token: String, @Path("id") id: Long): UserReply

    @GET("/api/me")
    suspend fun getMe(@Header("Authorization") token: String): UserReply

    @GET("/api/unread-counters")
    suspend fun getUnreadCounters(@Header("Authorization") token: String): UnreadCounters

    @POST("api/post/{post}/r")
    suspend fun recommendPost(@Header("Authorization") token: String, @Path("post") id: String, @Query("text") text: String? = null): Envelope

    @DELETE("api/post/{post}/r")
    suspend fun unrecommendPost(@Header("Authorization") token: String, @Path("post") id: String): Envelope

    @POST("api/post/{post}/s")
    suspend fun subscribeToPost(@Header("Authorization") token: String, @Path("post") id: String): Envelope

    @DELETE("api/post/{post}/s")
    suspend fun unsubscribeFromPost(@Header("Authorization") token: String, @Path("post") id: String): Envelope

    @POST("api/post/{post}/b")
    suspend fun bookmarkPost(@Header("Authorization") token: String, @Path("post") id: String): Envelope

    @DELETE("api/post/{post}/b")
    suspend fun unbookmarkPost(@Header("Authorization") token: String, @Path("post") id: String): Envelope

    @POST("api/post/{post}/pin")
    suspend fun pinPost(@Header("Authorization") token: String, @Path("post") id: String, @Query("text") text: String? = null): Envelope

    @DELETE("api/post/{post}/unpin")
    suspend fun unpinPost(@Header("Authorization") token: String, @Path("post") id: String): Envelope

    @POST("api/user/s/{login}")
    suspend fun subscribeToUser(@Header("Authorization") token: String, @Path("login") login: String): Envelope

    @DELETE("api/user/s/{login}")
    suspend fun unsubscribeFromUser(@Header("Authorization") token: String, @Path("login") login: String): Envelope

    @POST("api/user/sr/{login}")
    suspend fun subscribeToUserRecommendations(@Header("Authorization") token: String, @Path("login") login: String): Envelope

    @DELETE("api/user/sr/{login}")
    suspend fun unsubscribeFromUserRecommendations(@Header("Authorization") token: String, @Path("login") login: String): Envelope

    @POST("api/user/bl/{login}")
    suspend fun blockUser(@Header("Authorization") token: String, @Path("login") login: String): Envelope

    @DELETE("api/user/bl/{login}")
    suspend fun unblockUser(@Header("Authorization") token: String, @Path("login") login: String): Envelope
}