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
    private val userId = if (type == PostType.USER_POST) args[2] as Long else null
    private val tag = if (type == PostType.TAGGED_POST) args[2] as String else null

    private val repoFactory: RepoFactory = application.repoFactory
    private val api: PointAPI = application.mainApi
    private val avaRepository = application.avaRepo
    private val filesRepository = application.postFilesRepo

    private val postRepo = repoFactory.getPostRepo()

    private val recentPostRepo = repoFactory.getRecentPostRepo()
    private val commentedPostRepo = repoFactory.getCommentedPostRepo()
    private val allPostRepo = repoFactory.getAllPostRepo()
    private val userPostRepo = repoFactory.getUserPostRepo(userId ?: 0)
    private val taggedPostRepo = repoFactory.getTaggedPostRepo(tag ?: "")

    private val metaPost = getMetaPost(postId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, object : MetaPost() {
                override val id: String = postId
                override val authorId = 0L
            })

    val post = postRepo.getItem(postId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, Post(postId, 0L, ""))

    val files = filesRepository.getPostFiles(postId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val comments = postRepo.getPostComments(postId)
            .map { it.toMutableList().apply { sortBy { entry -> entry.number } } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val isPinVisible = post.map { it.authorId == state.id }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isPinned = post.map { it.isPinned }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isSubscribed = metaPost.map { it.bookmarked }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isRecommended = metaPost.map { it.recommended }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isBookmarked = metaPost.map { it.bookmarked }
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
            .flatMapConcat { postRepo.fetchPostAndComments(postId) }
            .flowOn(Dispatchers.IO)

    private fun getMetaPost(id: String) = when (type) {
        PostType.RECENT_POST -> recentPostRepo.getMetaPost(id)
        PostType.COMMENTED_POST -> commentedPostRepo.getMetaPost(id)
        PostType.ALL_POST -> allPostRepo.getMetaPost(id)
        PostType.USER_POST -> userPostRepo.getMetaPost(id)
        PostType.TAGGED_POST -> taggedPostRepo.getMetaPost(id)
    }

    private fun updatePost(post: MetaPost) = when (post) {
        is RecentPost -> recentPostRepo.updateMetaPost(post)
        is CommentedPost -> commentedPostRepo.updateMetaPost(post)
        is AllPost -> allPostRepo.updateMetaPost(post)
        is UserPost -> userPostRepo.updateMetaPost(post)
        is TaggedPost -> taggedPostRepo.updateMetaPost(post)
        else -> throw Exception("unknown model type")
    }

    fun fetchPostComments() =
            postRepo.fetchPostAndComments(postId).flowOn(Dispatchers.IO)

    private fun subscribe() = flow {
        val model = metaPost.value
        with(api.subscribeToPost(postId)) {
            checkSuccessful()
            model.subscribed = true
            updatePost(model)
            Log.d(this::class.simpleName, "subscribed to a post")
            emit(this)
        }
    }

    private fun unsubscribe() = flow {
        val model = metaPost.value
        with(api.unsubscribeFromPost(postId)) {
            checkSuccessful()
            model.subscribed = false
            updatePost(model)
            Log.d(this::class.simpleName, "unsubscribed from a post")
            emit(this)
        }
    }

    private fun recommend() = flow {
        val model = metaPost.value
        with(api.recommendPost(postId)) {
            checkSuccessful()
            model.recommended = true
            updatePost(model)
            emit(this)
        }
    }

    private fun unrecommend() = flow {
        val model = metaPost.value
        with(api.unrecommendPost(postId)) {
            checkSuccessful()
            model.recommended = false
            updatePost(model)
            emit(this)
        }
    }

    private fun bookmark() = flow {
        val model = metaPost.value
        with(api.bookmarkPost(postId)) {
            checkSuccessful()
            model.bookmarked = true
            updatePost(model)
            emit(this)
        }
    }

    private fun unbookmark() = flow {
        val model = metaPost.value
        with(api.unbookmarkPost(postId)) {
            checkSuccessful()
            model.bookmarked = false
            updatePost(model)
            emit(this)
        }
    }

    private fun pin() = flow {
        with(api.pinPost(postId)) {
            checkSuccessful()
            emit(this)
        }
    }

    private fun unpin() = flow {
        with(api.unpinPost(postId)) {
            checkSuccessful()
            emit(this)
        }
    }

    fun getAvatar(name: String) = avaRepository.getAvatar(name, Size.SIZE_80)
}