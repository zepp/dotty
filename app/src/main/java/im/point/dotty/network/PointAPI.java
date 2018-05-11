package im.point.dotty.network;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PointAPI {
    String BASE = "https://point.im";

    @FormUrlEncoded
    @POST("/api/login")
    Observable<LoginReply> login(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/logout")
    Observable<LogoutReply> logout(@Field("csrf_token") String token);
}
