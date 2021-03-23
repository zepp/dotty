/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

interface Repository<T, K> {
    fun getAll(): Flow<List<T>>
    fun getItem(id: K): Flow<T>
    fun fetch(): Flow<List<T>>
    fun purge()
}

fun <T> Flow<T>.toListFlow(): Flow<List<T>> = flow {
    with(mutableListOf<T>()) {
        emit(toList<T>(this))
    }
}