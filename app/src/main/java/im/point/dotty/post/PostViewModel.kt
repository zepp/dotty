/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.model.PostType
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.RepoFactory
import im.point.dotty.repository.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class PostViewModel(application: DottyApplication, private val post: PostType, private val postId: String)
    : ViewModel() {
    private val repoFactory: RepoFactory = application.repoFactory
    private val state: AppState = application.state
    private val api: PointAPI = application.mainApi
    private val avaRepository = application.avaRepo

    val isPinVisible = Channel<Boolean>(Channel.CONFLATED)

    fun getPost() = when (post) {
        PostType.RECENT_POST -> repoFactory.getRecentPostRepo().getItem(postId)
        PostType.COMMENTED_POST -> repoFactory.getCommentedPostRepo().getItem(postId)
        PostType.ALL_POST -> repoFactory.getAllPostRepo().getItem(postId)
        PostType.USER_POST -> repoFactory.getUserPostRepo().getItem(postId)
    }.onEach { isPinVisible.send(it.authorId == state.id) }
            .flowOn(Dispatchers.IO)

    fun getPostComments() = when (post) {
        PostType.RECENT_POST -> repoFactory.getRecentCommentRepo(postId).getAll()
        PostType.COMMENTED_POST -> repoFactory.getCommentedCommentRepo(postId).getAll()
        PostType.ALL_POST -> repoFactory.getAllCommentRepo(postId).getAll()
        PostType.USER_POST -> repoFactory.getUserCommentRepo(postId).getAll()
    }.flowOn(Dispatchers.IO)

    fun fetchPostComments() = when (post) {
        PostType.RECENT_POST -> repoFactory.getRecentCommentRepo(postId).fetchAll()
        PostType.COMMENTED_POST -> repoFactory.getCommentedCommentRepo(postId).fetchAll()
        PostType.ALL_POST -> repoFactory.getAllCommentRepo(postId).fetchAll()
        PostType.USER_POST -> repoFactory.getUserCommentRepo(postId).fetchAll()
    }.flowOn(Dispatchers.IO)

    fun subscribe() = viewModelScope.async(Dispatchers.IO) {
        with(api.subscribeToPost(postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun unsubscribe() = viewModelScope.async(Dispatchers.IO) {
        with(api.unsubscribeFromPost(postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun recommend() = viewModelScope.async(Dispatchers.IO) {
        with(api.recommendPost(postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun unrecommend() = viewModelScope.async(Dispatchers.IO) {
        with(api.unrecommendPost(postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun bookmark() = viewModelScope.async(Dispatchers.IO) {
        with(api.bookmarkPost(postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun unbookmark() = viewModelScope.async(Dispatchers.IO) {
        with(api.unbookmarkPost(postId)) {
            checkSuccessful()
            return@async this
        }
    }

    fun getAvatar(name: String) = avaRepository.getAvatar(name, Size.SIZE_40)
}