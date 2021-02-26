/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.common.Shared
import im.point.dotty.model.AllPost
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.LogoutReply
import im.point.dotty.network.SingleCallbackAdapter
import im.point.dotty.repository.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers

class MainViewModel internal constructor(application: DottyApplication) : AndroidViewModel(application) {
    private val repoFactory: RepoFactory
    private val recentRepo: RecentRepo
    private val commentedPostRepo: CommentedRepo
    private val allPostRepo: AllRepo
    private val userRepo: UserRepo
    private val shared: Shared = Shared(application.baseContext, application.state, application.mainApi)
    private val state: AppState = application.state
    private val api: AuthAPI = application.authApi

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

    fun fetchUnreadCounters(): Completable {
        return Completable.fromSingle(shared.fetchUnreadCounters())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun logout(): Completable {
        return Completable.fromSingle(Single.create { emitter: SingleEmitter<LogoutReply> ->
            api.logout(state.token ?: throw Exception("invalid token"),
                    state.csrfToken ?: throw Exception("invalid CSRF token"))
                    .enqueue(SingleCallbackAdapter(emitter))
        }
                .doFinally {
                    state.isLoggedIn = false
                    state.csrfToken = null
                    state.token = null
                    allPostRepo.purge()
                    recentRepo.purge()
                    commentedPostRepo.purge()
                    userRepo.purge()
                    shared.resetActivityBackStack()
                }.observeOn(AndroidSchedulers.mainThread()))
    }

    fun unreadPosts() = state.unreadPosts

    fun unreadComments() = state.unreadComments

    fun unreadPrivatePosts() = state.privateUnreadPosts

    fun unreadPrivateComments() = state.privateUnreadComments

    init {
        repoFactory = application.repoFactory
        recentRepo = repoFactory.getRecentRepo()
        commentedPostRepo = repoFactory.getCommentedRepo()
        allPostRepo = repoFactory.getAllRepo()
        userRepo = repoFactory.getUserRepo()
    }
}