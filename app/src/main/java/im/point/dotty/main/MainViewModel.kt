/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.db.DottyDatabase
import im.point.dotty.model.User
import im.point.dotty.network.PointAPI
import im.point.dotty.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel internal constructor(application: DottyApplication, vararg args: Any) : DottyViewModel(application) {
    private val repoFactory = application.repoFactory
    private val recentRepo = repoFactory.getRecentPostRepo()
    private val commentedRepo = repoFactory.getCommentedPostRepo()
    private val allRepo = repoFactory.getAllPostRepo()
    private val userRepo = repoFactory.getUserRepo()
    private val api: PointAPI = application.mainApi
    private val avaRepo = application.avaRepo
    private val fileRepository = application.postFilesRepo
    private val filesRepo = application.postFilesRepo
    private val db: DottyDatabase = application.database

    val user = userRepo.getMe()
        .stateIn(viewModelScope, SharingStarted.Eagerly, User(state.id, state.userLogin))
    val recent = recentRepo.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    val commented = commentedRepo.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    val all = allRepo.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun fetchRecent(isBefore: Boolean) =
            (if (isBefore) recentRepo.fetchBefore() else recentRepo.fetchAll())
                    .flowOn(Dispatchers.IO)

    fun fetchAll(isBefore: Boolean) =
            (if (isBefore) allRepo.fetchBefore() else allRepo.fetchAll())
                    .flowOn(Dispatchers.IO)

    fun fetchCommented(isBefore: Boolean) =
            (if (isBefore) commentedRepo.fetchBefore() else commentedRepo.fetchAll())
                    .flowOn(Dispatchers.IO)

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

    fun getPostImages(postId: String) = fileRepository.getPostFiles(postId)

    fun logout() = viewModelScope.async(Dispatchers.IO) {
        with(authAPI.logout(state.token, state.csrfToken)) {
            state.isLoggedIn = false
            resetActivityBackStack()
            this
        }
    }

    val unreadPosts = state.unreadPostsFlow
            .map { it.toString() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, "0")

    val unreadComments = state.unreadCommentsFlow
            .map { it.toString() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, "0")

    val unreadPrivatePosts = state.privateUnreadPostsFlow
            .map { it.toString() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, "0")

    val unreadPrivateComments = state.privateUnreadCommentsFlow
            .map { it.toString() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, "0")

    val title = user
            .map { it.formattedLogin }
            .stateIn(viewModelScope, SharingStarted.Eagerly, "dotty")

    override fun onCleared() {
        super.onCleared()
        GlobalScope.launch(Dispatchers.IO) {
            // clear DB out of the ViewModel scope
            if (!state.isLoggedIn) {
                db.clearAllTables()
                avaRepo.cleanupFileCache()
                filesRepo.cleanupFileCache()
            }
        }
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            launch { userRepo.fetchMe().flowOn(Dispatchers.IO).collect() }
            launch { fetchRecent(false).collect() }
            launch { fetchCommented(false).collect() }
            launch { fetchAll(false).collect() }
        }
    }
}