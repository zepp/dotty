/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.model.Comment
import im.point.dotty.model.Post
import im.point.dotty.model.PostType
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.RepoFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class PostViewModel(application: DottyApplication, private val post: PostType, private val postId: String)
    : ViewModel() {
    private val repoFactory: RepoFactory = application.repoFactory
    private val state: AppState = application.state
    private val api: PointAPI = application.mainApi

    val isPinVisible = Channel<Boolean>(Channel.CONFLATED)

    fun getPost(): Flow<Post> {
        return when (post) {
            PostType.RECENT_POST -> repoFactory.getRecentPostRepo().getItem(postId)
            PostType.COMMENTED_POST -> repoFactory.getCommentedPostRepo().getItem(postId)
            PostType.ALL_POST -> repoFactory.getAllCommentRepo(postId).getItem(postId)
            PostType.USER_POST -> repoFactory.getUserPostRepo().getItem(postId)
        }.map { it as Post }
                .onEach { isPinVisible.send(it.authorId == state.id) }
                .flowOn(Dispatchers.IO)
    }

    fun getPostComments(): Flow<List<Comment>> {
        return when (post) {
            PostType.RECENT_POST -> repoFactory.getRecentCommentRepo(postId).getAll()
            PostType.COMMENTED_POST -> repoFactory.getCommentedCommentRepo(postId).getAll()
            PostType.ALL_POST -> repoFactory.getAllCommentRepo(postId).getAll()
            PostType.USER_POST -> repoFactory.getUserCommentRepo(postId).getAll()
        }.flowOn(Dispatchers.IO)
    }

    fun fetchPostComments(): Flow<List<Comment>> {
        return when (post) {
            PostType.RECENT_POST -> repoFactory.getRecentCommentRepo(postId).fetch()
            PostType.COMMENTED_POST -> repoFactory.getCommentedCommentRepo(postId).fetch()
            PostType.ALL_POST -> repoFactory.getAllCommentRepo(postId).fetch()
            PostType.USER_POST -> repoFactory.getUserCommentRepo(postId).fetch()
        }.flowOn(Dispatchers.IO)
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