package im.point.dotty.network;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PointAPI {
    String BASE = "https://point.im";

    @POST("api/login")
    Observable<LoginReply> login(@Query("login") String login, @Query("password") String password);

    @POST("api/logout")
    Observable<LogoutReply> logout(@Query("csrf_token") String token);
}
