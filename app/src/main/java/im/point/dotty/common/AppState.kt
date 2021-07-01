/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@SuppressLint("ApplySharedPref")
class AppState (context: Context) {
    companion object {
        private val IS_LOGGED_IN = "is-logged-in"
        private val USER_ID = "user-id"
        private val USER_LOGIN = "user-name"
        private val TOKEN = "token"
        private val CSRF_TOKEN = "csrf-token"
        private val RECENT_PAGE_ID = "recent-last-id"
        private val COMMENTED_PAGE_ID = "comment-last-id"
        private val ALL_PAGE_ID = "all-last-id"
        private val UNREAD_POSTS = "unread-posts"
        private val UNREAD_COMMENTS = "unread-comments"
        private val PRIVATE_UNREAD_POSTS = "private-unread-posts"
        private val PRIVATE_UNREAD_COMMENTS = "private-unread-comments"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(this::class.simpleName,
            Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = preferences.getBoolean(IS_LOGGED_IN, false)
        set(value) = preferences.edit().run {
            putBoolean(IS_LOGGED_IN, value).commit()
        }

    var userLogin: String
        get() = preferences.getString(USER_LOGIN, null) ?: throw Exception("user login is null")
        set(value) = preferences.edit().run {
            if (value.isEmpty()) remove(USER_LOGIN).commit() else putString(USER_LOGIN, value).commit()
        }

    var token: String
        get() = preferences.getString(TOKEN, null) ?: throw Exception("API token is null")
        set(value) = preferences.edit().run {
            if (value.isEmpty()) remove(TOKEN).commit() else putString(TOKEN, value).commit()
        }

    var csrfToken: String
        get() = preferences.getString(CSRF_TOKEN, null) ?: throw Exception("CSRF token is null")
        set(value) = preferences.edit().run {
            if (value.isEmpty()) remove(CSRF_TOKEN).commit() else putString(CSRF_TOKEN, value).commit()
        }

    var id: Long
        get() = (preferences.getLong(USER_ID, -1)).also {
            if (it == -1L) throw Exception("user ID is null")
        }
        set(value) = preferences.edit().run {
            if (value == null) remove(USER_ID).commit() else putLong(USER_ID, value).commit()
        }

    var commentedPageId: Long?
        get() = with(preferences.getLong(COMMENTED_PAGE_ID, -1L)) {
            if (this == -1L) null else this
        }
        set(value) = preferences.edit().run {
            if (value == null) remove(COMMENTED_PAGE_ID).commit()
            else putLong(COMMENTED_PAGE_ID, value).commit()
        }

    var recentPageId: Long?
        get() = with(preferences.getLong(RECENT_PAGE_ID, -1L)) {
            if (this == -1L) null else this
        }
        set(value) = preferences.edit().run {
            if (value == null) remove(RECENT_PAGE_ID).commit()
            else putLong(RECENT_PAGE_ID, value).commit()
        }

    var allPageId: Long?
        get() = with(preferences.getLong(ALL_PAGE_ID, -1L)) {
            if (this == -1L) return null else this
        }
        set(value) = preferences.edit().run {
            if (value == null) remove(ALL_PAGE_ID).commit()
            else putLong(ALL_PAGE_ID, value).commit()
        }

    private val unreadPostsFlow_ = MutableSharedFlow<Int>(1)

    var unreadPosts: Int
        get() = preferences.getInt(UNREAD_POSTS, 0)
        set(value) {
            GlobalScope.launch { unreadPostsFlow_.emit(value) }
            preferences.edit().putInt(UNREAD_POSTS, value).commit()
        }

    val unreadPostsFlow = unreadPostsFlow_.asSharedFlow()

    private val unreadCommentsFlow_ = MutableSharedFlow<Int>(1)

    var unreadComments: Int
        get() = preferences.getInt(UNREAD_COMMENTS, 0)
        set(value) {
            GlobalScope.launch { unreadCommentsFlow_.emit(value) }
            preferences.edit().putInt(UNREAD_COMMENTS, value).commit()
        }

    val unreadCommentsFlow = unreadCommentsFlow_.asSharedFlow()

    private val privateUnreadPostsFlow_ = MutableSharedFlow<Int>(1)

    var privateUnreadPosts: Int
        get() = preferences.getInt(PRIVATE_UNREAD_POSTS, 0)
        set(value) {
            GlobalScope.launch { privateUnreadPostsFlow_.emit(value) }
            preferences.edit().putInt(PRIVATE_UNREAD_POSTS, value).commit()
        }

    val privateUnreadPostsFlow = privateUnreadPostsFlow_.asSharedFlow()

    private val privateUnreadCommentsFlow_ = MutableSharedFlow<Int>(1)

    var privateUnreadComments: Int
        get() = preferences.getInt(PRIVATE_UNREAD_COMMENTS, 0)
        set(value) {
            GlobalScope.launch { privateUnreadCommentsFlow_.emit(value) }
            preferences.edit().putInt(PRIVATE_UNREAD_COMMENTS, value).commit()
        }

    val privateUnreadCommentsFlow = privateUnreadCommentsFlow_.asSharedFlow()

    init {
        GlobalScope.launch {
            unreadPostsFlow_.emit(unreadPosts)
            unreadCommentsFlow_.emit(unreadComments)
            privateUnreadPostsFlow_.emit(privateUnreadPosts)
            privateUnreadCommentsFlow_.emit(privateUnreadComments)
        }
    }
}