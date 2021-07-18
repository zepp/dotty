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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
            db.clearAllTables()
            avaRepo.cleanupFileCache()
            filesRepo.cleanupFileCache()
            resetActivityBackStack()
            this
        }
    }

    fun unreadPosts() = state.unreadPostsFlow

    fun unreadComments() = state.unreadCommentsFlow

    fun unreadPrivatePosts() = state.privateUnreadPostsFlow

    fun unreadPrivateComments() = state.privateUnreadCommentsFlow

    init {
        viewModelScope.launch(exceptionHandler) {
            launch { userRepo.fetchMe().collect() }
            launch { fetchRecent(false).collect() }
            launch { fetchCommented(false).collect() }
            launch { fetchAll(false).collect() }
        }
    }
}