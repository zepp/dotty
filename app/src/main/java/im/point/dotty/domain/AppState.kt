package im.point.dotty.domain

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import im.point.dotty.R

class AppState (context: Context) {
    private val IS_LOGGED_IN = "is-logged-in"
    private val USER_NAME = "user-name"
    private val TOKEN = "token"
    private val CSRF_TOKEN = "csrf-token"
    private val RECENT_LAST_ID = "recent-last-id"
    private val COMMENTED_LAST_ID = "comment-last-id"
    private val ALL_LAST_ID = "all-last-id"

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

    var commentedLastId: String?
        get() = preferences.getString(COMMENTED_LAST_ID, null)
        set(value) {
            with (preferences.edit()) {
                if (value == null) remove(COMMENTED_LAST_ID).apply()
                else putString(COMMENTED_LAST_ID, value).apply()
            }
        }

    var recentLastId: String?
        get() = preferences.getString(RECENT_LAST_ID, null)
        set(value) {
            with (preferences.edit()) {
                if (value == null) remove(RECENT_LAST_ID).apply()
                else putString(RECENT_LAST_ID, value).apply()
            }
        }

    var allLastId: String?
        get() = preferences.getString(ALL_LAST_ID, null)
        set(value) {
            with (preferences.edit()) {
                if (value == null) remove(ALL_LAST_ID).apply()
                else putString(ALL_LAST_ID, value).apply()
            }
        }

    init {
        preferences = context.getSharedPreferences(this::class.simpleName, Context.MODE_PRIVATE)
        resources = context.resources
    }
}