package im.point.dotty.repository;

import java.util.List;

import im.point.dotty.db.AllPostDao;
import im.point.dotty.mapper.Mapper;
import im.point.dotty.model.AllPost;
import im.point.dotty.network.MetaPost;
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
    private final Mapper<AllPost, MetaPost> mapper;

    AllRepo(PointAPI api, String token, AllPostDao allPostDao, Mapper<AllPost, MetaPost> mapper) {
        this.api = api;
        this.allPostDao = allPostDao;
        this.token = token;
        this.mapper = mapper;
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
                .map(mapper::map)
                .toList()
                .doOnSuccess(allPosts -> allPostDao.insertAll(allPosts));
    }
}
