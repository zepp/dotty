/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.domain

import androidx.lifecycle.ViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.model.AllPost
import im.point.dotty.model.Comment
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.repository.RepoFactory
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

class PostViewModel(application: DottyApplication) : ViewModel() {
    private val repoFactory: RepoFactory
    private val shared: Shared = Shared(application.state, application.mainApi)

    fun getRecentPost(id: String): Flowable<RecentPost> {
        return repoFactory.getRecentRepo().getItem(id).observeOn(AndroidSchedulers.mainThread())
    }

    fun getCommentedPost(id: String): Flowable<CommentedPost> {
        return repoFactory.getCommentedRepo().getItem(id).observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllPost(id: String): Flowable<AllPost> {
        return repoFactory.getAllRepo().getItem(id).observeOn(AndroidSchedulers.mainThread())
    }

    fun getRecentPostComments(id: String): Flowable<List<Comment>> {
        return repoFactory.getRecentCommentRepo(id).getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun getCommentedPostComments(id: String): Flowable<List<Comment>> {
        return repoFactory.getCommentedCommentRepo(id).getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllPostComments(id: String): Flowable<List<Comment>> {
        return repoFactory.getAllCommentRepo(id).getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchRecentPostComments(id: String): Completable {
        return Completable.fromSingle(repoFactory.getRecentCommentRepo(id).fetch()
                .flatMap { shared.fetchUnreadCounters() })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchCommentedPostComments(id: String): Completable {
        return Completable.fromSingle(repoFactory.getCommentedCommentRepo(id).fetch()
                .flatMap { shared.fetchUnreadCounters() })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchAllPostComments(id: String): Completable {
        return Completable.fromSingle(repoFactory.getAllCommentRepo(id).fetch()
                .flatMap { shared.fetchUnreadCounters() })
                .observeOn(AndroidSchedulers.mainThread())
    }

    init {
        repoFactory = application.repoFactory
    }
}