package im.point.dotty.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PointAPI {
    String BASE = "https://point.im";

    @GET("/api/recent")
    Call<PostsReply> getRecent(@Header("Authorization") String token, @Query("before") String before);

    @GET("/api/all")
    Call<PostsReply> getAll(@Header("Authorization") String token, @Query("before") String before);

    @GET("/api/comments")
    Call<PostsReply> getComments(@Header("Authorization") String token, @Query("before") String before);

    @GET("/api/blog/{user}")
    Call<PostsReply> getUserPosts(@Header("Authorization") String token, @Path("user") String user, @Query("before") String before);
}
