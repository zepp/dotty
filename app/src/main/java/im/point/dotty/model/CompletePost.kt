/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

abstract class CompletePost<T : MetaPost> {
    abstract val metapost: T
    abstract val post: Post

    val id: String
        get() = post.id

    val authorId: Long
        get() = post.authorId

    val authorLogin: String
        get() = post.authorLogin
}