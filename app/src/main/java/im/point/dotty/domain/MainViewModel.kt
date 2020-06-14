package im.point.dotty.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import im.point.dotty.model.AllPost
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.repository.RepoFactory
import im.point.dotty.repository.Repository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

class MainViewModel internal constructor(application: Application) : AndroidViewModel(application) {
    private val repoFactory: RepoFactory
    private val recentPostRepository: Repository<RecentPost>
    private val commentedPostRepository: Repository<CommentedPost>
    private val allPostRepository: Repository<AllPost>

    fun fetchRecent(): Completable {
        return Completable.fromSingle(recentPostRepository.fetch()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getRecent(): Flowable<List<RecentPost>> {
        return recentPostRepository.getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchAll(): Completable {
        return Completable.fromSingle(allPostRepository.fetch()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getAll(): Flowable<List<AllPost>> {
        return allPostRepository.getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchCommented(): Completable {
        return Completable.fromSingle(commentedPostRepository.fetch()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getCommented(): Flowable<List<CommentedPost>> {
        return commentedPostRepository.getAll().observeOn(AndroidSchedulers.mainThread())
    }

    init {
        repoFactory = RepoFactory(application.baseContext)
        recentPostRepository = repoFactory.getRecentRepo()
        commentedPostRepository = repoFactory.getCommentedRepo()
        allPostRepository = repoFactory.getAllRepo()
    }
}