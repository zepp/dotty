package im.point.dotty.repository;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import im.point.dotty.db.DottyDatabase;
import im.point.dotty.domain.AppState;
import im.point.dotty.mapper.AllPostMapper;
import im.point.dotty.mapper.CommentedPostMapper;
import im.point.dotty.mapper.RecentPostMapper;
import im.point.dotty.model.AllPost;
import im.point.dotty.model.CommentedPost;
import im.point.dotty.model.RecentPost;
import im.point.dotty.network.PointAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RepoFactory {
    private final Gson gson;
    private final Retrofit retrofit;
    private final PointAPI api;
    private final DottyDatabase database;
    private final AppState state;

    public RepoFactory(Context context) {
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(PointAPI.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(PointAPI.class);
        this.database = Room.databaseBuilder(context.getApplicationContext(), DottyDatabase.class, "main").build();
        this.state = AppState.getInstance(context);
    }

    public Repository<RecentPost> getRecentRepo() {
        return new RecentRepo(api, state.getToken(), database.getRecentPostDao(), new RecentPostMapper());
    }

    public Repository<CommentedPost> getCommentedRepo() {
        return new CommentedRepo(api, state.getToken(), database.getCommentedPostDao(), new CommentedPostMapper());
    }

    public Repository<AllPost> getAllRepo() {
        return new AllRepo(api, state.getToken(), database.getAllPostDao(), new AllPostMapper());
    }
}
