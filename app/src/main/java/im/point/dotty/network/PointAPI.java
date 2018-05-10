package im.point.dotty.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PointAPI {
    String BASE = "https://point.im";

    @GET("api/login")
    Observable<LoginReply> login(@Query("login") String login, @Query("password") String password);

    @GET("api/logout")
    Observable<LogoutReply> logout(@Query("csrf_token") String token);
}
