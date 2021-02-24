package im.point.dotty.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PointAPI {
    @GET("/api/recent")
    fun getRecent(@Header("Authorization") token: String, @Query("before") before: Long?): Call<PostsReply>

    @GET("/api/all")
    fun getAll(@Header("Authorization") token: String, @Query("before") before: Long?): Call<PostsReply>

    @GET("/api/comments")
    fun getComments(@Header("Authorization") token: String, @Query("before") before: Long?): Call<PostsReply>

    @GET("/api/bookmarks")
    fun getBookmarks(@Header("Authorization") token: String, @Query("before") before: Long?): Call<PostsReply>

    @GET("/api/tags")
    fun getTagged(@Header("Authorization") token: String, @Query("tag") tag: String, @Query("before") before: Long?): Call<PostsReply>

    @GET("/api/tags/{user}")
    fun getUserTagged(@Header("Authorization") token: String, @Path("user") user: String, @Query("tag") tag: String, @Query("before") before: Long?): Call<PostsReply>

    @GET("/api/blog/{user}")
    fun getUserPosts(@Header("Authorization") token: String, @Path("user") user: String, @Query("before") before: Long?): Call<PostsReply>

    @GET("/api/unread-counters")
    fun getCounters(@Header("Authorization") token: String): Call<CountersReply?>

    @GET("/api/post/{post}")
    fun getPost(@Header("Authorization") token: String, @Path("post") id: String): Call<PostReply>

    @GET("/api/user/login/{login}")
    fun getUser(@Header("Authorization") token: String, @Path("login") login: String): Call<UserReply>

    @GET("/api/user/id/{id}")
    fun getUser(@Header("Authorization") token: String, @Path("id") id: Long): Call<UserReply>

    @GET("/api/me")
    fun getMe(@Header("Authorization") token: String): Call<UserReply>

    @GET("/api/unread-counters")
    fun getUnreadCounters(@Header("Authorization") token: String): Call<UnreadCounters>
}