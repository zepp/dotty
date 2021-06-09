/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PostViewModel(application: DottyApplication, vararg args: Any)
    : DottyViewModel(application) {

    private val type = args[0] as PostType
    private val postId = args[1] as String

    private val repoFactory: RepoFactory = application.repoFactory
    private val api: PointAPI = application.mainApi
    private val avaRepository = application.avaRepo

    private val onSubscribe_ = MutableSharedFlow<Boolean>(0)
    private val onRecommend_ = MutableSharedFlow<Boolean>(0)
    private val onBookmark_ = MutableSharedFlow<Boolean>(0)

    val onSubscribe = onSubscribe_.distinctUntilChanged()
    val onRecommend = onRecommend_.distinctUntilChanged()
    val onBookmark = onBookmark_.distinctUntilChanged()
    val post = getPost(postId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, object : Post() {
                override val id: String = postId
                override val authorId = 0L
            })
    val isPinVisible = post.map { it.authorId == state.id }
            .distinctUntilChanged()
    val comments = getPostComments(postId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            launch { fetchPostComments().collect() }
        }
    }

    fun onSubscribeChecked(value: Boolean) = viewModelScope.launch {
        onSubscribe_.emit(value)
    }

    fun onRecommendChecked(value: Boolean) = viewModelScope.launch {
        onRecommend_.emit(value)
    }

    fun onBookmarkChecked(value: Boolean) = viewModelScope.launch {
        onBookmark_.emit(value)
    }

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

    fun subscribe() = viewModelScope.async(Dispatchers.IO) {
        val model = post.value
        if (model.subscribed == false) {
            with(api.subscribeToPost(postId)) {
                checkSuccessful()
                model.subscribed = true
                updatePost(model)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unsubscribe() = viewModelScope.async(Dispatchers.IO) {
        val model = post.value
        if (model.subscribed == true) {
            with(api.unsubscribeFromPost(postId)) {
                checkSuccessful()
                model.subscribed = false
                updatePost(model)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun recommend() = viewModelScope.async(Dispatchers.IO) {
        val model = post.value
        if (model.recommended == false) {
            with(api.recommendPost(postId)) {
                checkSuccessful()
                model.recommended = true
                updatePost(model)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unrecommend() = viewModelScope.async(Dispatchers.IO) {
        val model = post.value
        if (model.recommended == true) {
            with(api.unrecommendPost(postId)) {
                checkSuccessful()
                model.recommended = false
                updatePost(model)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun bookmark() = viewModelScope.async(Dispatchers.IO) {
        val model = post.value
        if (model.bookmarked == false) {
            with(api.bookmarkPost(postId)) {
                checkSuccessful()
                model.bookmarked = true
                updatePost(model)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun unbookmark() = viewModelScope.async(Dispatchers.IO) {
        val model = post.value
        if (model.bookmarked == true) {
            with(api.unbookmarkPost(postId)) {
                checkSuccessful()
                model.bookmarked = false
                updatePost(model)
                return@async this
            }
        }
        return@async Envelope()
    }

    fun getAvatar(name: String) = avaRepository.getAvatar(name, Size.SIZE_40)
}