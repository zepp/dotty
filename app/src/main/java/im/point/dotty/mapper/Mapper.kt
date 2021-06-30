/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

interface Mapper<T, in R> {
    fun map(entry: R): T
}