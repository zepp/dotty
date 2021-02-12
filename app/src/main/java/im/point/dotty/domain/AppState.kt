package im.point.dotty.domain

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources

class AppState (context: Context) {
    private val IS_LOGGED_IN = "is-logged-in"
    private val USER_NAME = "user-name"
    private val TOKEN = "token"
    private val CSRF_TOKEN = "csrf-token"
    private val RECENT_PAGE_ID = "recent-last-id"
    private val COMMENTED_PAGE_ID = "comment-last-id"
    private val ALL_PAGE_ID = "all-last-id"

    private val preferences: SharedPreferences
    private val resources: Resources

    var isLoggedIn: Boolean?
        get() = preferences.getBoolean(IS_LOGGED_IN, false)
        set(value) {
            with (preferences.edit()) {
                if (value == null) remove(IS_LOGGED_IN).apply() else putBoolean(IS_LOGGED_IN, value).apply()
            }
        }

    var userName: String?
        get() = preferences.getString(USER_NAME, null)
        set(value) {
            with(preferences.edit()) {
                if (value == null) remove(USER_NAME).apply() else putString(USER_NAME, value).apply()
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
            with (preferences.edit()) {
                if (value == null) remove(CSRF_TOKEN).apply() else putString(CSRF_TOKEN, value).apply()
            }
        }

    var commentedPageId: Long?
        get() {
            return with(preferences.getLong(COMMENTED_PAGE_ID, -1L)) {
                if (this == -1L) null else this
            }
        }
        set(value) {
            with (preferences.edit()) {
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
            with (preferences.edit()) {
                if (value == null) remove(ALL_PAGE_ID).apply()
                else putLong(ALL_PAGE_ID, value).apply()
            }
        }

    init {
        preferences = context.getSharedPreferences(this::class.simpleName, Context.MODE_PRIVATE)
        resources = context.resources
    }
}