package im.point.dotty.domain

import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.model.AllPost
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.repository.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

class MainViewModel internal constructor(application: DottyApplication) : AndroidViewModel(application) {
    private val repoFactory: RepoFactory
    private val recentRepo : RecentRepo
    private val commentedPostRepo: CommentedRepo
    private val allPostRepo: AllRepo

    fun fetchRecent(isBefore: Boolean): Completable {
        return Completable.fromSingle(if (isBefore) recentRepo.fetchBefore() else recentRepo.fetch())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getRecent(): Flowable<List<RecentPost>> {
        return recentRepo.getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchAll(isBefore: Boolean): Completable {
        return Completable.fromSingle(if (isBefore) allPostRepo.fetchBefore() else allPostRepo.fetch())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAll(): Flowable<List<AllPost>> {
        return allPostRepo.getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchCommented(isBefore: Boolean): Completable {
        return Completable.fromSingle(if (isBefore) commentedPostRepo.fetchBefore() else commentedPostRepo.fetch())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCommented(): Flowable<List<CommentedPost>> {
        return commentedPostRepo.getAll().observeOn(AndroidSchedulers.mainThread())
    }

    init {
        repoFactory = application.repoFactory
        recentRepo = repoFactory.getRecentRepo()
        commentedPostRepo = repoFactory.getCommentedRepo()
        allPostRepo = repoFactory.getAllRepo()
    }
}