/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import androidx.annotation.IdRes
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.model.*
import im.point.dotty.network.Envelope
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.RepoFactory
import im.point.dotty.repository.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class PostViewModel(application: DottyApplication, private val post: PostType, private val postId: String)
    : DottyViewModel(application) {
    private val repoFactory: RepoFactory = application.repoFactory
    private val api: PointAPI = application.mainApi
    private val avaRepository = application.avaRepo

    val isPinVisible = Channel<Boolean>(Channel.CONFLATED)

    private fun getPost(id: String) = when (post) {
        PostType.RECENT_POST -> repoFactory.getRecentPostRepo().getItem(id)
        PostType.COMMENTED_POST -> repoFactory.getCommentedPostRepo().getItem(id)
        PostType.ALL_POST -> repoFactory.getAllPostRepo().getItem(id)
        PostType.USER_POST -> repoFactory.getUserPostRepo().getItem(id)
    }

    private fun updatePost(model: Post) = when (model) {
        is RecentPost -> repoFactory.getRecentPostRepo().updateItem(model)
        is CommentedPost -> repoFactory.getCommentedPostRepo().updateItem(model)
        is AllPost -> repoFactory.getAllPostRepo().updateItem(model)
        is UserPost -> repoFactory.getUserPostRepo().updateItem(model)
        else -> throw Exception("unknown model type")
    }

    fun getPost() = getPost(postId)
            .onEach { isPinVisible.send(it.authorId == state.id) }
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
        val post = getPost(postId).first()
        if (post.subscribed == false) {
            with(api.subscribeToPost(postId)) {
                checkSuccessful()
                post.subscribed = true
                updatePost(post)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unsubscribe() = viewModelScope.async(Dispatchers.IO) {
        val post = getPost(postId).first()
        if (post.subscribed == true) {
            with(api.unsubscribeFromPost(postId)) {
                checkSuccessful()
                post.subscribed = false
                updatePost(post)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun recommend() = viewModelScope.async(Dispatchers.IO) {
        val post = getPost(postId).first()
        if (post.recommended == false) {
            with(api.recommendPost(postId)) {
                checkSuccessful()
                post.recommended = true
                updatePost(post)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unrecommend() = viewModelScope.async(Dispatchers.IO) {
        val post = getPost(postId).first()
        if (post.recommended == true) {
            with(api.unrecommendPost(postId)) {
                checkSuccessful()
                post.recommended = false
                updatePost(post)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun bookmark() = viewModelScope.async(Dispatchers.IO) {
        val post = getPost(postId).first()
        if (post.bookmarked == false) {
            with(api.bookmarkPost(postId)) {
                checkSuccessful()
                post.bookmarked = true
                updatePost(post)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unbookmark() = viewModelScope.async(Dispatchers.IO) {
        val post = getPost(postId).first()
        if (post.bookmarked == true) {
            with(api.unbookmarkPost(postId)) {
                checkSuccessful()
                post.bookmarked = false
                updatePost(post)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun getAvatar(name: String) = avaRepository.getAvatar(name, Size.SIZE_40)

    internal data class Event(@IdRes val id: Int, val isChecked: Boolean)
}