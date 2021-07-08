/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.network.PointAPI

class RepoFactory(private val api: PointAPI, private val database: DottyDatabase, private val state: AppState) {

    fun getRecentPostRepo() = RecentPostRepo(api, state, database)

    fun getCommentedPostRepo() = CommentedPostRepo(api, state, database)

    fun getAllPostRepo() = AllPostRepo(api, state, database)

    fun getUserPostRepo(userId: Long = 0) = UserPostRepo(api, database, userId)

    fun getTaggedPostRepo(tag: String) = TaggedPostRepo(api, database, tag)

    fun getUserRepo() = UserRepo(api, state, database)

    fun getPostRepo() = PostRepo(api, database)
}