/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.model.AllPost
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainViewModel internal constructor(application: DottyApplication, vararg args: Any) : DottyViewModel(application) {
    private val repoFactory = application.repoFactory
    private val recentRepo = repoFactory.getRecentPostRepo()
    private val commentedRepo = repoFactory.getCommentedPostRepo()
    private val allRepo = repoFactory.getAllPostRepo()
    private val userRepo = repoFactory.getUserRepo()
    private val api: PointAPI = application.mainApi
    private val avaRepo = application.avaRepo

    fun fetchRecent(isBefore: Boolean): Flow<List<RecentPost>> {
        return (if (isBefore) recentRepo.fetchBefore() else recentRepo.fetchAll())
                .flowOn(Dispatchers.IO)
    }

    fun getRecent(): Flow<List<RecentPost>> {
        return recentRepo.getAll().flowOn(Dispatchers.IO)
    }

    fun fetchAll(isBefore: Boolean): Flow<List<AllPost>> {
        return (if (isBefore) allRepo.fetchBefore() else allRepo.fetchAll())
                .flowOn(Dispatchers.IO)
    }

    fun getAll(): Flow<List<AllPost>> {
        return allRepo.getAll().flowOn(Dispatchers.IO)
    }

    fun fetchCommented(isBefore: Boolean): Flow<List<CommentedPost>> {
        return (if (isBefore) commentedRepo.fetchBefore() else commentedRepo.fetchAll())
                .flowOn(Dispatchers.IO)
    }

    fun getCommented(): Flow<List<CommentedPost>> {
        return commentedRepo.getAll().flowOn(Dispatchers.IO)
    }

    fun fetchUnreadCounters() = viewModelScope.async(Dispatchers.IO) {
        with(api.getUnreadCounters()) {
            checkSuccessful()
            state.unreadPosts = this.posts
            state.unreadComments = this.comments
            state.privateUnreadPosts = this.privatePosts
            state.privateUnreadComments = this.privateComments
            return@async this
        }
    }

    fun getAvatar(name: String) = avaRepo.getAvatar(name, Size.SIZE_80)

    fun logout() = viewModelScope.async(Dispatchers.IO) {
        with(authAPI.logout(state.token, state.csrfToken)) {
            state.isLoggedIn = false
            allRepo.purge()
            recentRepo.purge()
            commentedRepo.purge()
            userRepo.purge()
            resetActivityBackStack()
            this
        }
    }

    fun unreadPosts() = state.unreadPostsFlow

    fun unreadComments() = state.unreadCommentsFlow

    fun unreadPrivatePosts() = state.privateUnreadPostsFlow

    fun unreadPrivateComments() = state.privateUnreadCommentsFlow

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            launch { fetchRecent(false).collect() }
            launch { fetchCommented(false).collect() }
            launch { fetchAll(false).collect() }
        }
    }
}