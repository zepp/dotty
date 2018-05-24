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
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
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

    public Completable fetchRecent() {
        Observable<PostsReply> source = Observable.create(emitter -> {
            api.getRecent(state.getToken(), null).enqueue(new ObservableCallBackAdapte<>(emitter));
        });
        source.observeOn(Schedulers.io())
                .flatMap(postsReply -> Observable.fromIterable(postsReply.getPosts()))
                .subscribe(new DisposableObserver<MetaPost>() {
                    final List<RecentPost> list = new ArrayList<>();

                    @Override
                    public void onNext(MetaPost post) {
                        list.add(PostMapper.mapRecentPost(post));
                    }

                    @Override
                    public void onComplete() {
                        recentPostDao.insertAll(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
        return Completable.fromObservable(source).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<RecentPost>> getRecent() {
        return recentPostDao.getAll().observeOn(AndroidSchedulers.mainThread());
    }

    public Completable fetchAll() {
        Observable<PostsReply> source = Observable.create(emitter -> {
            api.getAll(state.getToken(), null).enqueue(new ObservableCallBackAdapte<>(emitter));
        });
        source.observeOn(Schedulers.io())
                .flatMap(reply -> Observable.fromIterable(reply.getPosts()))
                .subscribe(new DisposableObserver<MetaPost>() {
                    final List<AllPost> list = new ArrayList<>();

                    @Override
                    public void onNext(MetaPost post) {
                        list.add(PostMapper.mapAllPost(post));
                    }

                    @Override
                    public void onComplete() {
                        allPostDao.insertAll(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
        return Completable.fromObservable(source).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<AllPost>> getAll() {
        return allPostDao.getAll().observeOn(AndroidSchedulers.mainThread());
    }

    public Completable fetchCommented() {
        Observable<PostsReply> source = Observable.create(emitter -> {
            api.getComments(state.getToken(), null).enqueue(new ObservableCallBackAdapte<>(emitter));
        });
        source.observeOn(Schedulers.io())
                .flatMap(reply -> Observable.fromIterable(reply.getPosts()))
                .subscribe(new DisposableObserver<MetaPost>() {
                    final List<CommentedPost> list = new ArrayList<>();

                    @Override
                    public void onNext(MetaPost post) {
                        list.add(PostMapper.mapCommentedPost(post));
                    }

                    @Override
                    public void onComplete() {
                        commentedPostDao.insertAll(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

        return Completable.fromObservable(source).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<CommentedPost>> getCommented() {
        return commentedPostDao.getAll().observeOn(AndroidSchedulers.mainThread());
    }
}
