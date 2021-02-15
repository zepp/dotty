package im.point.dotty.repository

import io.reactivex.Flowable
import io.reactivex.Single

interface Repository<T> {
    fun getAll(): Flowable<List<T>>
    fun getItem(id: String): Flowable<T>
    fun fetch(): Single<List<T>>
}