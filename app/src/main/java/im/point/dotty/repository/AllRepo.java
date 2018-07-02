package im.point.dotty.repository;

import java.util.List;

import im.point.dotty.db.AllPostDao;
import im.point.dotty.mapper.PostMapper;
import im.point.dotty.model.AllPost;
import im.point.dotty.network.ObservableCallBackAdapter;
import im.point.dotty.network.PointAPI;
import im.point.dotty.network.PostsReply;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

class AllRepo implements Repository<AllPost> {
    private final PointAPI api;
    private final AllPostDao allPostDao;
    private final String token;

    AllRepo(PointAPI api, String token, AllPostDao allPostDao) {
        this.api = api;
        this.allPostDao = allPostDao;
        this.token = token;
    }

    @Override
    public Flowable<List<AllPost>> getAll() {
        return allPostDao.getAll();
    }

    @Override
    public Single<List<AllPost>> fetch() {
        Observable<PostsReply> source = Observable.create(emitter -> {
            api.getAll(token, "").enqueue(new ObservableCallBackAdapter<>(emitter));
        });
        return source.observeOn(Schedulers.io())
                .flatMap(reply -> Observable.fromIterable(reply.getPosts()))
                .map(PostMapper::mapAllPost)
                .toList()
                .doOnSuccess(allPosts -> allPostDao.insertAll(allPosts));
    }
}
