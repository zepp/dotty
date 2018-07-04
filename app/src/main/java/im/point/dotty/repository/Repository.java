package im.point.dotty.repository;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface Repository<T> {
    Flowable<List<T>> getAll();

    Single<List<T>> fetch();
}
