package im.point.dotty.domain;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import im.point.dotty.db.AllPostDao;
import im.point.dotty.db.CommentedPostDao;
import im.point.dotty.db.DottyDatabase;
import im.point.dotty.db.RecentPostDao;
import im.point.dotty.mapper.PostMapper;
import im.point.dotty.model.AllPost;
import im.point.dotty.model.CommentedPost;
import im.point.dotty.model.RecentPost;
import im.point.dotty.network.ObservableCallBackAdapter;
import im.point.dotty.network.PointAPI;
import im.point.dotty.network.PostsReply;
import im.point.dotty.repository.RepoFactory;
import im.point.dotty.repository.Repository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class MainInteractor extends Interactor {
    private RepoFactory repoFactory;
    private Repository<RecentPost> recentPostRepository;
    private Repository<CommentedPost> commentedPostRepository;
    private Repository<AllPost> allPostRepository;

    public MainInteractor() {
    }

    @Override
    public void onCreate(Context applicationContext) {
        repoFactory = new RepoFactory(applicationContext);
        recentPostRepository = repoFactory.getRecentRepo();
        commentedPostRepository = repoFactory.getCommentedRepo();
        allPostRepository = repoFactory.getAllRepo();
    }

    public Completable fetchRecent() {
        return Completable.fromSingle(recentPostRepository.fetch()).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<RecentPost>> getRecent() {
        return recentPostRepository.getAll().observeOn(AndroidSchedulers.mainThread());
    }

    public Completable fetchAll() {
        return Completable.fromSingle(allPostRepository.fetch()).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<AllPost>> getAll() {
        return allPostRepository.getAll().observeOn(AndroidSchedulers.mainThread());
    }

    public Completable fetchCommented() {
        return Completable.fromSingle(commentedPostRepository.fetch()).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<CommentedPost>> getCommented() {
        return commentedPostRepository.getAll().observeOn(AndroidSchedulers.mainThread());
    }
}
