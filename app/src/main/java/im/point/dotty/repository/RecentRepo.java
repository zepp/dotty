package im.point.dotty.repository;

import java.util.List;

import im.point.dotty.db.RecentPostDao;
import im.point.dotty.mapper.PostMapper;
import im.point.dotty.model.RecentPost;
import im.point.dotty.network.ObservableCallBackAdapter;
import im.point.dotty.network.PointAPI;
import im.point.dotty.network.PostsReply;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

class RecentRepo implements Repository<RecentPost> {
    private final PointAPI api;
    private final RecentPostDao recentPostDao;
    private final String token;

    RecentRepo(PointAPI api, String token, RecentPostDao recentPostDao) {
        this.api = api;
        this.token = token;
        this.recentPostDao = recentPostDao;
    }

    @Override
    public Flowable<List<RecentPost>> getAll() {
        return recentPostDao.getAll();
    }

    @Override
    public Single<List<RecentPost>> fetch() {
        Observable<PostsReply> source = Observable.create(emitter -> {
            api.getRecent(token, "").enqueue(new ObservableCallBackAdapter<>(emitter));
        });
        return source.observeOn(Schedulers.io())
                .flatMap(postsReply -> Observable.fromIterable(postsReply.getPosts()))
                .map(PostMapper::mapRecentPost)
                .toList()
                .doOnSuccess(recentPosts -> recentPostDao.insertAll(recentPosts));
    }
}
