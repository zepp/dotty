package im.point.dotty.domain;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import im.point.dotty.model.AllPost;
import im.point.dotty.model.CommentedPost;
import im.point.dotty.model.RecentPost;
import im.point.dotty.repository.RepoFactory;
import im.point.dotty.repository.Repository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public final class MainViewModel extends AndroidViewModel {
    private RepoFactory repoFactory;
    private Repository<RecentPost> recentPostRepository;
    private Repository<CommentedPost> commentedPostRepository;
    private Repository<AllPost> allPostRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repoFactory = new RepoFactory(application.getBaseContext());
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
