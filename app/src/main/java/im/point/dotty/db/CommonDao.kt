/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface CommonDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: T)

    @Update
    fun updateItem(item: T)
}