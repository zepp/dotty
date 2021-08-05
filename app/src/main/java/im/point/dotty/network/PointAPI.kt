/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import retrofit2.http.*

interface PointAPI {
    @GET("/api/recent")
    suspend fun getRecent(@Query("before") before: Long?): PostsReply

    @GET("/api/all")
    suspend fun getAll(@Query("before") before: Long?): PostsReply

    @GET("/api/comments")
    suspend fun getComments(@Query("before") before: Long?): PostsReply

    @GET("/api/bookmarks")
    suspend fun getBookmarks(@Query("before") before: Long?): PostsReply

    @GET("/api/tags")
    suspend fun getTagged(@Query("tag") tag: String, @Query("before") before: Long?): PostsReply

    @GET("/api/tags/{user}")
    suspend fun getUserTagged(
        @Path("user") user: String,
        @Query("tag") tag: String,
        @Query("before") before: Long?
    ): PostsReply

    @GET("/api/tags/{user}")
    suspend fun getUserTags(@Path("user") user: String): List<UserTagEntry>

    @GET("/api/blog/{user}")
    suspend fun getUserPosts(@Path("user") user: String, @Query("before") before: Long?): PostsReply

    @GET("/api/post/{post}")
    suspend fun getPost(@Path("post") id: String): PostReply

    @DELETE("/api/post/{post}")
    suspend fun deletePost(@Path("post") id: String): Envelope

    @GET("/api/user/login/{login}")
    suspend fun getUser(@Path("login") login: String): UserReply

    @GET("/api/user/id/{id}")
    suspend fun getUser(@Path("id") id: Long): UserReply

    @GET("/api/me")
    suspend fun getMe(): UserReply

    @GET("/api/unread-counters")
    suspend fun getUnreadCounters(): UnreadCounters

    @FormUrlEncoded
    @POST("api/post/{post}/r")
    suspend fun recommendPost(
            @Path("post") id: String,
            @Field("text") text: String? = null
    ): Envelope

    @DELETE("api/post/{post}/r")
    suspend fun unrecommendPost(@Path("post") id: String): Envelope

    @POST("api/post/{post}/s")
    suspend fun subscribeToPost(@Path("post") id: String): Envelope

    @DELETE("api/post/{post}/s")
    suspend fun unsubscribeFromPost(@Path("post") id: String): Envelope

    @POST("api/post/{post}/b")
    suspend fun bookmarkPost(@Path("post") id: String): Envelope

    @DELETE("api/post/{post}/b")
    suspend fun unbookmarkPost(@Path("post") id: String): Envelope

    @FormUrlEncoded
    @POST("api/post/{post}/pin")
    suspend fun pinPost(@Path("post") id: String, @Field("text") text: String? = null): Envelope

    @DELETE("api/post/{post}/unpin")
    suspend fun unpinPost(@Path("post") id: String): Envelope

    @POST("api/user/s/{login}")
    suspend fun subscribeToUser(@Path("login") login: String): Envelope

    @DELETE("api/user/s/{login}")
    suspend fun unsubscribeFromUser(@Path("login") login: String): Envelope

    @POST("api/user/sr/{login}")
    suspend fun subscribeToUserRecommendations(@Path("login") login: String): Envelope

    @DELETE("api/user/sr/{login}")
    suspend fun unsubscribeFromUserRecommendations(@Path("login") login: String): Envelope

    @POST("api/user/bl/{login}")
    suspend fun blockUser(@Path("login") login: String): Envelope

    @DELETE("api/user/bl/{login}")
    suspend fun unblockUser(@Path("login") login: String): Envelope

    @FormUrlEncoded
    @POST("/api/post/{id}")
    suspend fun addComment(
            @Path("id") id: String,
            @Field("text") text: String,
            @Field("comment_id") relyTo: Int? = null,
    ): Envelope

    @FormUrlEncoded
    @PATCH("/api/post/{id}/{number}")
    suspend fun editComment(
            @Path("id") id: String,
            @Path("number") number: Int,
            @Field("text") text: String,
    ): EditCommentReply

    @DELETE("/api/post/{id}/{number}")
    suspend fun deleteComment(
            @Path("id") id: String,
            @Path("number") number: Int
    ): Envelope

    @FormUrlEncoded
    @POST("/api/post/{id}/{number}/r")
    suspend fun recommendComment(
            @Path("id") id: String,
            @Path("number") number: Int,
            @Field("text") text: String,
    ): Envelope
}