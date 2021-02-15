package im.point.dotty.mapper

interface Mapper<T, in R> {
    fun map(entry: R): T
    fun merge(model: T, entry: R): T
}