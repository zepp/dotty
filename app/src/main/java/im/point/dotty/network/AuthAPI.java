package im.point.dotty.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthAPI {
    String BASE = "https://point.im";

    @FormUrlEncoded
    @POST("/api/login")
    Call<LoginReply> login(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/logout")
    Call<LogoutReply> logout(@Field("csrf_token") String token);
}
