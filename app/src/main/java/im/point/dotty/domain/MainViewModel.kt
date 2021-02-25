package im.point.dotty.domain

import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.model.AllPost
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.model.User
import im.point.dotty.repository.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class MainViewModel internal constructor(application: DottyApplication) : AndroidViewModel(application) {
    private val repoFactory: RepoFactory
    private val recentRepo: RecentRepo
    private val commentedPostRepo: CommentedRepo
    private val allPostRepo: AllRepo
    private val userRepo: UserRepo
    private val shared: Shared = Shared(application.state, application.mainApi)
    private val state: AppState = application.state

    fun fetchRecent(isBefore: Boolean): Completable {
        return Completable.fromSingle(if (isBefore) recentRepo.fetchBefore() else recentRepo.fetch()
                .flatMap { shared.fetchUnreadCounters() })
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

    fun getMe(): Flowable<User> {
        return userRepo.getMe().observeOn(AndroidSchedulers.mainThread())
    }

    fun getUser(id: Long): Flowable<User> {
        return userRepo.getItem(id).observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchUser(id: Long): Single<User> {
        return userRepo.fetchUser(id).observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchUnreadCounters(): Completable {
        return Completable.fromSingle(shared.fetchUnreadCounters())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUnreadPosts() = state.unreadPosts

    fun getUnreadComments() = state.unreadComments

    fun getUnreadPrivatePosts() = state.privateUnreadPosts

    fun getUnreadPrivateComments() = state.privateUnreadComments

    init {
        repoFactory = application.repoFactory
        recentRepo = repoFactory.getRecentRepo()
        commentedPostRepo = repoFactory.getCommentedRepo()
        allPostRepo = repoFactory.getAllRepo()
        userRepo = repoFactory.getUserRepo()
    }
}