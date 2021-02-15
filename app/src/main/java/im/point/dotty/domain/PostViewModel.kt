package im.point.dotty.domain

import androidx.lifecycle.ViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.model.AllPost
import im.point.dotty.model.Comment
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.repository.RepoFactory
import io.reactivex.Flowable

class PostViewModel(application: DottyApplication) : ViewModel() {
    private val repoFactory: RepoFactory

    fun getRecentPostComments(post: RecentPost): Flowable<List<Comment>> {
        return repoFactory.getRecentCommentRepo(post).getAll();
    }

    fun getCommentedPostComments(post: CommentedPost): Flowable<List<Comment>> {
        return repoFactory.getCommentedCommentRepo(post).getAll();
    }

    fun getAllPostComments(post: AllPost): Flowable<List<Comment>> {
        return repoFactory.getAllCommentRepo(post).getAll();
    }

    init {
        repoFactory = application.repoFactory
    }
}