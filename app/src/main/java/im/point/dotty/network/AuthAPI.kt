package im.point.dotty.network

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthAPI {
    @FormUrlEncoded
    @POST("/api/login")
    fun login(@Field("login") login: String, @Field("password") password: String): Call<LoginReply?>

    @FormUrlEncoded
    @POST("/api/logout")
    fun logout(@Header("Authorization") token: String, @Field("csrf_token") csrfToken: String): Call<LogoutReply?>
}