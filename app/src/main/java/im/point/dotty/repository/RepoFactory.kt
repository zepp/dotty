package im.point.dotty.repository

import im.point.dotty.db.DottyDatabase
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.CommentMapper
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

    fun getCommentRepo(postId : String) : Repository<Comment> {
        return CommentRepo(api, state, database.getCommentDao(), postId)
    }
}