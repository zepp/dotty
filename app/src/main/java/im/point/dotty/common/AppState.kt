/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

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
    private val resources: Resources = context.resources

    var isLoggedIn: Boolean?
        get() = preferences.getBoolean(IS_LOGGED_IN, false)
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(IS_LOGGED_IN).apply() else putBoolean(IS_LOGGED_IN, value).apply()
            }
        }

    var userLogin: String?
        get() = preferences.getString(USER_LOGIN, null)
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(USER_LOGIN).apply() else putString(USER_LOGIN, value).apply()
            }
        }

    var token: String?
        get() = preferences.getString(TOKEN, null)
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(TOKEN).apply() else putString(TOKEN, value).apply()
            }
        }

    var csrfToken: String?
        get() = preferences.getString(CSRF_TOKEN, null)
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(CSRF_TOKEN).apply() else putString(CSRF_TOKEN, value).apply()
            }
        }

    var id: Long?
        get() {
            return with(preferences.getLong(USER_ID, -1)) {
                if (this == -1L) null else this
            }
        }
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(USER_ID).apply() else putLong(USER_ID, value).apply()
            }
        }

    var commentedPageId: Long?
        get() {
            return with(preferences.getLong(COMMENTED_PAGE_ID, -1L)) {
                if (this == -1L) null else this
            }
        }
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(COMMENTED_PAGE_ID).apply()
                else putLong(COMMENTED_PAGE_ID, value).apply()
            }
        }

    var recentPageId: Long?
        get() {
            return with(preferences.getLong(RECENT_PAGE_ID, -1L)) {
                if (this == -1L) null else this
            }
        }
        set(value) {
            with (preferences.edit()) {
                if (value == null) remove(RECENT_PAGE_ID).apply()
                else putLong(RECENT_PAGE_ID, value).apply()
            }
        }

    var allPageId: Long?
        get() {
            return with(preferences.getLong(ALL_PAGE_ID, -1L)) {
                if (this == -1L) return null else this
            }
        }
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(ALL_PAGE_ID).apply()
                else putLong(ALL_PAGE_ID, value).apply()
            }
        }

    val unreadPosts: Observable<Int> =
            with(Producer(preferences, UNREAD_POSTS, preferences::getInt, 0)) {
                Observable.create(this)
                        .doOnDispose(this::dispose)
                        .distinctUntilChanged()
                        .replay(1)
                        .refCount()
            }

    fun updateUnreadPosts(value: Int) = preferences.edit().putInt(UNREAD_POSTS, value).apply()

    val unreadComments: Observable<Int> =
            with(Producer(preferences, UNREAD_COMMENTS, preferences::getInt, 0)) {
                Observable.create(this)
                        .doOnDispose(this::dispose)
                        .distinctUntilChanged()
                        .replay(1)
                        .refCount()
            }

    fun updateUnreadComments(value: Int) = preferences.edit().putInt(UNREAD_COMMENTS, value).apply()

    val privateUnreadPosts: Observable<Int> =
            with(Producer(preferences, PRIVATE_UNREAD_POSTS, preferences::getInt, 0)) {
                Observable.create(this)
                        .doOnDispose(this::dispose)
                        .distinctUntilChanged()
                        .replay(1)
                        .refCount()
            }

    fun updatePrivateUnreadPosts(value: Int) = preferences.edit().putInt(PRIVATE_UNREAD_POSTS, value).apply()

    val privateUnreadComments: Observable<Int> =
            with(Producer(preferences, PRIVATE_UNREAD_COMMENTS, preferences::getInt, 0)) {
                Observable.create(this)
                        .doOnDispose(this::dispose)
                        .distinctUntilChanged()
                        .replay(1)
                        .refCount()
            }

    fun updatePrivateUnreadComments(value: Int) = preferences.edit().putInt(PRIVATE_UNREAD_COMMENTS, value).apply()
}

internal class Producer<T>(private val prefs: SharedPreferences,
                           private val key: String,
                           private val getter: (key: String, value: T) -> T,
                           private val defValue: T) :
        ObservableOnSubscribe<T>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var emitter: ObservableEmitter<T>

    override fun subscribe(emitter: ObservableEmitter<T>) {
        this.emitter = emitter
        prefs.registerOnSharedPreferenceChangeListener(this)
        emitter.onNext(getter(key, defValue))
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        if (key == this.key) {
            emitter.onNext(getter(key, defValue))
        }
    }

    fun dispose() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }
}