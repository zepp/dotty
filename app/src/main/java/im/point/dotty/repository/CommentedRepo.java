package im.point.dotty.repository;

import java.util.List;

import im.point.dotty.db.CommentedPostDao;
import im.point.dotty.mapper.Mapper;
import im.point.dotty.model.CommentedPost;
import im.point.dotty.network.MetaPost;
import im.point.dotty.network.ObservableCallBackAdapter;
import im.point.dotty.network.PointAPI;
import im.point.dotty.network.PostsReply;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

class CommentedRepo implements Repository<CommentedPost> {
    private final PointAPI api;
    private final CommentedPostDao commentedPostDao;
    private final String token;
    private final Mapper<CommentedPost, MetaPost> mapper;

    CommentedRepo(PointAPI api, String token, CommentedPostDao commentedPostDao, Mapper<CommentedPost, MetaPost> mapper) {
        this.api = api;
        this.commentedPostDao = commentedPostDao;
        this.token = token;
        this.mapper = mapper;
    }

    @Override
    public Flowable<List<CommentedPost>> getAll() {
        return commentedPostDao.getAll();
    }

    @Override
    public Single<List<CommentedPost>> fetch() {
        Observable<PostsReply> source = Observable.create(emitter -> {
            api.getComments(token, "").enqueue(new ObservableCallBackAdapter<>(emitter));
        });
        return source.observeOn(Schedulers.io())
                .flatMap(reply -> Observable.fromIterable(reply.getPosts()))
                .map(mapper::map)
                .toList()
                .doOnSuccess(commentedPosts -> commentedPostDao.insertAll(commentedPosts));
    }
}
