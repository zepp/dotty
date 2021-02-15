package im.point.dotty.repository

import im.point.dotty.db.DottyDatabase
import im.point.dotty.domain.AppState
import im.point.dotty.model.Comment
import im.point.dotty.network.PointAPI

class RepoFactory(private val api: PointAPI, private val database: DottyDatabase, private val state: AppState) {

    fun getRecentRepo(): RecentRepo {
        return RecentRepo(api, state, database.getRecentPostDao())
    }

    fun getCommentedRepo(): CommentedRepo {
        return CommentedRepo(api, state, database.getCommentedPostDao())
    }

    fun getAllRepo(): AllRepo {
        return AllRepo(api, state, database.getAllPostDao())
    }

    fun getRecentCommentRepo(id: String): Repository<Comment> {
        val dao = database.getRecentPostDao()
        return CommentRepo(api, state, database.getCommentDao(), dao, dao.getPost(id))
    }

    fun getCommentedCommentRepo(id: String): Repository<Comment> {
        val dao = database.getCommentedPostDao()
        return CommentRepo(api, state, database.getCommentDao(), dao, dao.getPost(id))
    }

    fun getAllCommentRepo(id: String): Repository<Comment> {
        val dao = database.getAllPostDao()
        return CommentRepo(api, state, database.getCommentDao(), dao, dao.getPost(id))
    }
}