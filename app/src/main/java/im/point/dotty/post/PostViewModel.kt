/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.common.Shared
import im.point.dotty.model.*
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.RepoFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class PostViewModel(application: DottyApplication, private val postId: String) : ViewModel() {
    private val repoFactory: RepoFactory = application.repoFactory
    private val state: AppState = application.state
    private val api: PointAPI = application.mainApi
    private val shared: Shared = Shared(application.baseContext, application.state, application.mainApi)

    val isPinVisible = Channel<Boolean>()

    fun getRecentPost(): Flow<RecentPost> {
        return repoFactory.getRecentPostRepo().getItem(postId)
                .onEach { isPinVisible.send(it.authorId == state.id) }
                .flowOn(Dispatchers.IO)
    }

    fun getCommentedPost(): Flow<CommentedPost> {
        return repoFactory.getCommentedPostRepo().getItem(postId)
                .onEach { isPinVisible.send(it.authorId == state.id) }
                .flowOn(Dispatchers.IO)
    }

    fun getAllPost(): Flow<AllPost> {
        return repoFactory.getAllPostRepo().getItem(postId)
                .onEach { isPinVisible.send(it.authorId == state.id) }
                .flowOn(Dispatchers.IO)
    }

    fun getUserPost(): Flow<UserPost> {
        return repoFactory.getUserPostRepo().getItem(postId)
                .onEach { isPinVisible.send(it.authorId == state.id) }
                .flowOn(Dispatchers.IO)
    }

    fun getRecentPostComments(): Flow<List<Comment>> {
        return repoFactory.getRecentCommentRepo(postId).getAll().flowOn(Dispatchers.IO)
    }

    fun getCommentedPostComments(): Flow<List<Comment>> {
        return repoFactory.getCommentedCommentRepo(postId).getAll().flowOn(Dispatchers.IO)
    }

    fun getAllPostComments(): Flow<List<Comment>> {
        return repoFactory.getAllCommentRepo(postId).getAll().flowOn(Dispatchers.IO)
    }

    fun getUserPostComments(): Flow<List<Comment>> {
        return repoFactory.getUserCommentRepo(postId).getAll().flowOn(Dispatchers.IO)
    }

    fun fetchRecentPostComments(): Flow<List<Comment>> {
        return repoFactory.getRecentCommentRepo(postId).fetch()
                .onCompletion { shared.fetchUnreadCounters() }
                .flowOn(Dispatchers.IO)
    }

    fun fetchCommentedPostComments(): Flow<List<Comment>> {
        return repoFactory.getCommentedCommentRepo(postId).fetch()
                .onCompletion { shared.fetchUnreadCounters() }
                .flowOn(Dispatchers.IO)
    }

    fun fetchAllPostComments(): Flow<List<Comment>> {
        return repoFactory.getAllCommentRepo(postId).fetch()
                .onCompletion { shared.fetchUnreadCounters() }
                .flowOn(Dispatchers.IO)
    }

    fun fetchUserPostComments(): Flow<List<Comment>> {
        return repoFactory.getUserCommentRepo(postId).fetch().flowOn(Dispatchers.IO)
    }

    fun subscribe() = viewModelScope.async(Dispatchers.IO) {
        with(api.subscribeToPost(state.token, postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun unsubscribe() = viewModelScope.async(Dispatchers.IO) {
        with(api.unsubscribeFromPost(state.token, postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun recommend() = viewModelScope.async(Dispatchers.IO) {
        with(api.recommendPost(state.token, postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun unrecommend() = viewModelScope.async(Dispatchers.IO) {
        with(api.unrecommendPost(state.token, postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun bookmark() = viewModelScope.async(Dispatchers.IO) {
        with(api.bookmarkPost(state.token, postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun unbookmark() = viewModelScope.async(Dispatchers.IO) {
        with(api.unbookmarkPost(state.token, postId)) {
            checkSuccessful()
            return@async this
        }
    }
}