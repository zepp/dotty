/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import kotlinx.coroutines.flow.Flow

interface Repository<T, K> {
    fun getAll(): Flow<List<T>>
    fun fetchAll(): Flow<List<T>>
    fun getItem(id: K): Flow<T>
}
