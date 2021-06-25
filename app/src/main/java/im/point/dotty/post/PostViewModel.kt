/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.util.Log
import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.model.*
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.RepoFactory
import im.point.dotty.repository.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
class PostViewModel(application: DottyApplication, vararg args: Any)
    : DottyViewModel(application) {

    private val type = args[0] as PostType
    private val postId = args[1] as String

    private val repoFactory: RepoFactory = application.repoFactory
    private val api: PointAPI = application.mainApi
    private val avaRepository = application.avaRepo

    val post = getPost(postId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, object : Post() {
                override val id: String = postId
                override val authorId = 0L
            })
    val comments = getPostComments(postId)
            .map { it.toMutableList().apply { sortBy { entry -> entry.number } } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    val isPinVisible = post.map { it.authorId == state.id }
            .distinctUntilChanged()
    val isSubscribed = post.map { it.subscribed == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isRecommended = post.map { it.recommended == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isBookmarked = post.map { it.bookmarked == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isPinned = post.map { it.pinned == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            launch { fetchPostComments().collect() }
        }
    }

    fun onSubscribeChecked(value: Boolean) = flowOf(value)
            .filter { it.xor(isSubscribed.value) }
            .flatMapConcat { if (it) subscribe() else unsubscribe() }
            .flowOn(Dispatchers.IO)

    fun onRecommendChecked(value: Boolean) = flowOf(value)
            .filter { it.xor(isRecommended.value) }
            .flatMapConcat { if (it) recommend() else unrecommend() }
            .flowOn(Dispatchers.IO)

    fun onBookmarkChecked(value: Boolean) = flowOf(value)
            .filter { it.xor(isBookmarked.value) }
            .flatMapConcat { if (it) bookmark() else unbookmark() }
            .flowOn(Dispatchers.IO)

    fun onPinChecked(value: Boolean) = flowOf(value)
            .filter { it.xor(isPinned.value) }
            .flatMapConcat { if (it) pin() else unpin() }
            .flowOn(Dispatchers.IO)

    private fun getPost(id: String) = when (type) {
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

    private fun getPostComments(id:String) = when (type) {
        PostType.RECENT_POST -> repoFactory.getRecentCommentRepo(id).getAll()
        PostType.COMMENTED_POST -> repoFactory.getCommentedCommentRepo(id).getAll()
        PostType.ALL_POST -> repoFactory.getAllCommentRepo(id).getAll()
        PostType.USER_POST -> repoFactory.getUserCommentRepo(id).getAll()
    }

    fun fetchPostComments() = when (type) {
        PostType.RECENT_POST -> repoFactory.getRecentCommentRepo(postId).fetchAll()
        PostType.COMMENTED_POST -> repoFactory.getCommentedCommentRepo(postId).fetchAll()
        PostType.ALL_POST -> repoFactory.getAllCommentRepo(postId).fetchAll()
        PostType.USER_POST -> repoFactory.getUserCommentRepo(postId).fetchAll()
    }.flowOn(Dispatchers.IO)

    private fun subscribe() = flow {
        val model = post.value
        with(api.subscribeToPost(postId)) {
            checkSuccessful()
            model.subscribed = true
            updatePost(model)
            Log.d(this::class.simpleName, "subscribed to a post")
            emit(this)
        }
    }

    private fun unsubscribe() = flow {
        val model = post.value
        with(api.unsubscribeFromPost(postId)) {
            checkSuccessful()
            model.subscribed = false
            updatePost(model)
            Log.d(this::class.simpleName, "unsubscribed from a post")
            emit(this)
        }
    }

    private fun recommend() = flow {
        val model = post.value
        with(api.recommendPost(postId)) {
            checkSuccessful()
            model.recommended = true
            updatePost(model)
            emit(this)
        }
    }

    private fun unrecommend() = flow {
        val model = post.value
        with(api.unrecommendPost(postId)) {
            checkSuccessful()
            model.recommended = false
            updatePost(model)
            emit(this)
        }
    }

    private fun bookmark() = flow {
        val model = post.value
        with(api.bookmarkPost(postId)) {
            checkSuccessful()
            model.bookmarked = true
            updatePost(model)
            emit(this)
        }
    }

    private fun unbookmark() = flow {
        val model = post.value
        with(api.unbookmarkPost(postId)) {
            checkSuccessful()
            model.bookmarked = false
            updatePost(model)
            emit(this)
        }
    }

    private fun pin() = flow {
        val model = post.value
        with(api.pinPost(postId)) {
            checkSuccessful()
            model.pinned = true
            updatePost(model)
            emit(this)
        }
    }

    private fun unpin() = flow {
        val model = post.value
        with(api.unpinPost(postId)) {
            checkSuccessful()
            model.pinned = false
            updatePost(model)
            emit(this)
        }
    }

    fun getAvatar(name: String) = avaRepository.getAvatar(name, Size.SIZE_40)
}