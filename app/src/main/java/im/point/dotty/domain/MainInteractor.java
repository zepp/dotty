package im.point.dotty.domain;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import im.point.dotty.db.AllPostDao;
import im.point.dotty.db.CommentedPostDao;
import im.point.dotty.db.DottyDatabase;
import im.point.dotty.db.RecentPostDao;
import im.point.dotty.mapper.PostMapper;
import im.point.dotty.model.AllPost;
import im.point.dotty.model.CommentedPost;
import im.point.dotty.model.RecentPost;
import im.point.dotty.network.MetaPost;
import im.point.dotty.network.PointAPI;
import im.point.dotty.network.PostsReply;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class MainInteractor extends Interactor {
    private final Gson gson;
    private final Retrofit retrofit;
    private final PointAPI api;
    private AppState state;
    private DottyDatabase database;
    private RecentPostDao recentPostDao;
    private CommentedPostDao commentedPostDao;
    private AllPostDao allPostDao;

    public MainInteractor() {
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(PointAPI.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(PointAPI.class);
    }

    @Override
    public void onCreate(Context applicationContext) {
        this.state = AppState.getInstance(applicationContext);
        this.database = Room.databaseBuilder(applicationContext, DottyDatabase.class, "main").build();
        this.recentPostDao = database.getRecentPostDao();
        this.commentedPostDao = database.getCommentedPostDao();
        this.allPostDao = database.getAllPostDao();
    }

    public Single<List<MetaPost>> fetchRecent() {
        Single<PostsReply> single = Single.create(emitter -> {
            api.getRecent(state.getToken(), null).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        single.observeOn(Schedulers.io())
                .map(reply -> reply.getPosts())
                .subscribe(new DisposableSingleObserver<List<MetaPost>>() {
                    @Override
                    public void onSuccess(List<MetaPost> metaPosts) {
                        List<RecentPost> posts = new ArrayList<>();
                        for (MetaPost post : metaPosts) {
                            posts.add(PostMapper.mapRecentPost(post));
                        }
                        recentPostDao.addPosts(posts);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
        return single.map(reply -> reply.getPosts()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<MetaPost>> fetchAll() {
        Single<PostsReply> single = Single.create(emitter -> {
            api.getAll(state.getToken(), null).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        single.observeOn(Schedulers.io())
                .map(reply -> reply.getPosts())
                .subscribe(new DisposableSingleObserver<List<MetaPost>>() {
                    @Override
                    public void onSuccess(List<MetaPost> metaPosts) {
                        List<AllPost> posts = new ArrayList<>();
                        for (MetaPost post : metaPosts) {
                            posts.add(PostMapper.mapAllPost(post));
                        }
                        allPostDao.addPosts(posts);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
        return single.map(reply -> reply.getPosts()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<MetaPost>> fetchCommented() {
        Single<PostsReply> single = Single.create(emitter -> {
           api.getComments(state.getToken(), null).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        single.observeOn(Schedulers.io())
                .map(reply -> reply.getPosts())
                .subscribe(new DisposableSingleObserver<List<MetaPost>>() {
                    @Override
                    public void onSuccess(List<MetaPost> metaPosts) {
                        List<CommentedPost> posts = new ArrayList<>();
                        for (MetaPost post : metaPosts) {
                            posts.add(PostMapper.mapCommentedPost(post));
                        }
                        commentedPostDao.addPosts(posts);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

        return single.map(reply -> reply.getPosts()).observeOn(AndroidSchedulers.mainThread());
    }
}
