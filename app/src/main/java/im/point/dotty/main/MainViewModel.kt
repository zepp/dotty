/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.common.Shared
import im.point.dotty.model.AllPost
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.network.AuthAPI
import im.point.dotty.network.UnreadCounters
import im.point.dotty.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class MainViewModel internal constructor(application: DottyApplication) : AndroidViewModel(application) {
    private val repoFactory: RepoFactory
    private val recentRepo: RecentPostRepo
    private val commentedRepo: CommentedPostRepo
    private val allRepo: AllPostRepo
    private val userRepo: UserRepo
    private val shared: Shared = Shared(application.baseContext, application.state, application.mainApi)
    private val state: AppState = application.state
    private val api: AuthAPI = application.authApi

    fun fetchRecent(isBefore: Boolean): Flow<List<RecentPost>> {
        return (if (isBefore) recentRepo.fetchBefore() else recentRepo.fetch())
                .flowOn(Dispatchers.IO)
    }

    fun getRecent(): Flow<List<RecentPost>> {
        return recentRepo.getAll().flowOn(Dispatchers.IO)
    }

    fun fetchAll(isBefore: Boolean): Flow<List<AllPost>> {
        return (if (isBefore) allRepo.fetchBefore() else allRepo.fetch())
                .flowOn(Dispatchers.IO)
    }

    fun getAll(): Flow<List<AllPost>> {
        return allRepo.getAll().flowOn(Dispatchers.IO)
    }

    fun fetchCommented(isBefore: Boolean): Flow<List<CommentedPost>> {
        return (if (isBefore) commentedRepo.fetchBefore() else commentedRepo.fetch())
                .flowOn(Dispatchers.IO)
    }

    fun getCommented(): Flow<List<CommentedPost>> {
        return commentedRepo.getAll().flowOn(Dispatchers.IO)
    }

    fun fetchUnreadCounters(): Flow<UnreadCounters> {
        return shared.fetchUnreadCounters().flowOn(Dispatchers.IO)
    }

    fun logout() = viewModelScope.async(Dispatchers.IO) {
        try {
            api.logout(state.token, state.csrfToken)
        } finally {
            state.isLoggedIn = false
            allRepo.purge()
            recentRepo.purge()
            commentedRepo.purge()
            userRepo.purge()
        }
        withContext(Dispatchers.Main) {
            shared.resetActivityBackStack()
        }
    }

    fun unreadPosts() = state.unreadPosts

    fun unreadComments() = state.unreadComments

    fun unreadPrivatePosts() = state.privateUnreadPosts

    fun unreadPrivateComments() = state.privateUnreadComments

    init {
        repoFactory = application.repoFactory
        recentRepo = repoFactory.getRecentPostRepo()
        commentedRepo = repoFactory.getCommentedPostRepo()
        allRepo = repoFactory.getAllPostRepo()
        userRepo = repoFactory.getUserRepo()
    }
}