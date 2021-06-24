/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.repository

import im.point.dotty.common.AppState
import im.point.dotty.db.DottyDatabase
import im.point.dotty.model.Comment
import im.point.dotty.network.PointAPI

class RepoFactory(private val api: PointAPI, private val database: DottyDatabase, private val state: AppState) {

    fun getRecentPostRepo(): RecentPostRepo {
        return RecentPostRepo(api, state, database.getRecentPostDao())
    }

    fun getCommentedPostRepo(): CommentedPostRepo {
        return CommentedPostRepo(api, state, database.getCommentedPostDao())
    }

    fun getAllPostRepo(): AllPostRepo {
        return AllPostRepo(api, state, database.getAllPostDao())
    }

    fun getUserPostRepo(userId: Long = 0): UserPostRepo {
        return UserPostRepo(api, database.getUserDao(), database.getUserPostsDao(), userId)
    }

    fun getUserRepo(): UserRepo {
        return UserRepo(api, state, database.getUserDao())
    }

    fun getRecentCommentRepo(id: String): Repository<Comment, String> {
        val dao = database.getRecentPostDao()
        return CommentRepo(api, database.getCommentDao(), dao, id, dao.getItemFlow(id))
    }

    fun getCommentedCommentRepo(id: String): Repository<Comment, String> {
        val dao = database.getCommentedPostDao()
        return CommentRepo(api, database.getCommentDao(), dao, id, dao.getItemFlow(id))
    }

    fun getAllCommentRepo(id: String): Repository<Comment, String> {
        val dao = database.getAllPostDao()
        return CommentRepo(api, database.getCommentDao(), dao, id, dao.getItemFlow(id))
    }

    fun getUserCommentRepo(id: String): Repository<Comment, String> {
        val dao = database.getUserPostsDao()
        return CommentRepo(api, database.getCommentDao(), dao, id, dao.getItemFlow(id))
    }
}