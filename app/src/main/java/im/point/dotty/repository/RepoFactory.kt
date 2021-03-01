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

    fun getUserRepo(): UserRepo {
        return UserRepo(api, state, database.getUserDao())
    }

    fun getAllPostRepo(): AllPostRepo {
        return AllPostRepo(api, state, database.getAllPostDao())
    }

    fun getRecentCommentRepo(id: String): Repository<Comment, String> {
        val dao = database.getRecentPostDao()
        return CommentRepo(api, state, database.getCommentDao(), dao, dao.getPost(id))
    }

    fun getCommentedCommentRepo(id: String): Repository<Comment, String> {
        val dao = database.getCommentedPostDao()
        return CommentRepo(api, state, database.getCommentDao(), dao, dao.getPost(id))
    }

    fun getAllCommentRepo(id: String): Repository<Comment, String> {
        val dao = database.getAllPostDao()
        return CommentRepo(api, state, database.getCommentDao(), dao, dao.getPost(id))
    }
}