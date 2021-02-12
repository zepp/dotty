package im.point.dotty.repository

import im.point.dotty.db.DottyDatabase
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.AllPostMapper
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.CommentedPostMapper
import im.point.dotty.mapper.RecentPostMapper
import im.point.dotty.model.AllPost
import im.point.dotty.model.Comment
import im.point.dotty.model.CommentedPost
import im.point.dotty.network.PointAPI

class RepoFactory(private val api: PointAPI, private val database: DottyDatabase, private val state: AppState) {

    fun getRecentRepo(): RecentRepo {
        return RecentRepo(api, state, database.getRecentPostDao(), RecentPostMapper())
    }

    fun getCommentedRepo(): CommentedRepo {
        return CommentedRepo(api, state, database.getCommentedPostDao(), CommentedPostMapper())
    }

    fun getAllRepo(): AllRepo {
        return AllRepo(api, state, database.getAllPostDao(), AllPostMapper())
    }

    fun getCommentRepo(postId : String) : Repository<Comment> {
        return CommentRepo(api, state.token ?: throw Exception("invalid token"), database.getCommentDao(), CommentMapper(), postId)
    }
}