/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

abstract class CompletePost<T : MetaPost> {
    abstract val metapost: T
    abstract val post: Post
    abstract val comment: Comment?

    val id: String
        get() = post.id

    val authorId: Long
        get() {
            return if (comment == null) {
                post.authorId
            } else {
                comment!!.userId
            }
        }

    val authorLogin: String
        get() {
            return if (comment == null) {
                post.authorLogin
            } else {
                comment!!.login
            }
        }

    val isComment: Boolean
        get() = comment != null
}