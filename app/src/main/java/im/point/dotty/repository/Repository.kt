/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import io.reactivex.Flowable
import io.reactivex.Single

interface Repository<T, K> {
    fun getAll(): Flowable<List<T>>
    fun getItem(id: K): Flowable<T>
    fun fetch(): Single<List<T>>
    fun purge()
}