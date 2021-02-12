package im.point.dotty.domain

import android.app.Application
import androidx.lifecycle.ViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.model.Comment
import im.point.dotty.repository.RepoFactory
import io.reactivex.Flowable

class PostViewModel(application: DottyApplication) : ViewModel() {
    private val repoFactory: RepoFactory

    fun getComments(postId : String) : Flowable<List<Comment>> {
        return repoFactory.getCommentRepo(postId).getAll();
    }

    init {
        repoFactory = application.repoFactory
    }
}