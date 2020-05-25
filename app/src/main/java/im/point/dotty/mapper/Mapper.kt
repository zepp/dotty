package im.point.dotty.mapper

interface Mapper<T, R> {
    fun map(entry: R): T
}