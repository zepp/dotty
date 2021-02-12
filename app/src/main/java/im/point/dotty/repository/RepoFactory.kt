package im.point.dotty.repository

import im.point.dotty.DottyApplication
import im.point.dotty.db.DottyDatabase
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.AllPostMapper
import im.point.dotty.mapper.CommentMapper
import im.point.dotty.mapper.CommentedPostMapper
import im.point.dotty.mapper.RecentPostMapper
import im.point.dotty.model.AllPost
import im.point.dotty.model.Comment
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.network.PointAPI

class RepoFactory(private val api: PointAPI, private val database: DottyDatabase, private val state: AppState) {

    fun getRecentRepo(): Repository<RecentPost> {
        return RecentRepo(api, state.token ?: throw Exception("invalid token"), database.getRecentPostDao(), RecentPostMapper())
    }

    fun getCommentedRepo(): Repository<CommentedPost> {
        return CommentedRepo(api, state.token ?: throw Exception("invalid token"), database.getCommentedPostDao(), CommentedPostMapper())
    }

    fun getAllRepo(): Repository<AllPost> {
        return AllRepo(api, state.token ?: throw Exception("invalid token"), database.getAllPostDao(), AllPostMapper())
    }

    fun getCommentRepo(postId : String) : Repository<Comment> {
        return CommentRepo(api, state.token ?: throw Exception("invalid token"), database.getCommentDao(), CommentMapper(), postId)
    }
}