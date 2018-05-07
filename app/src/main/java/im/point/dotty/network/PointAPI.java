package im.point.dotty.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PointAPI {
    @GET("api/login")
    Call<LoginReply> login(@Query("login") String login, @Query("password") String password);

    @GET("api/logout")
    Call<LogoutReply> logout(@Query("csrf_token") String token);
}
