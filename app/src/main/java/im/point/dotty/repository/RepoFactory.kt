/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.network.PointAPI

class RepoFactory(private val api: PointAPI, private val database: DottyDatabase, private val state: AppState) {
    private val postRepo_ by lazy { PostRepo(api, database) }

    fun getPostRepo() = postRepo_

    fun getRecentPostRepo() = RecentPostRepo(api, state, database, postRepo_)

    fun getCommentedPostRepo() = CommentedPostRepo(api, state, database, postRepo_)

    fun getAllPostRepo() = AllPostRepo(api, state, database, postRepo_)

    fun getUserPostRepo(userId: Long = 0) = UserPostRepo(api, database, postRepo_, userId)

    fun getTaggedPostRepo(tag: String) = TaggedPostRepo(api, database, postRepo_, tag)

    fun getUserRepo() = UserRepo(api, state, database)
}