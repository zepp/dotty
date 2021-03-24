/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import im.point.dotty.login.LoginActivity
import im.point.dotty.main.MainActivity
import im.point.dotty.network.PointAPI
import im.point.dotty.network.UnreadCounters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Shared(private val context: Context, private val state: AppState, private val api: PointAPI) : ContextWrapper(context) {
    fun fetchUnreadCounters(): Flow<UnreadCounters> {
        return flow {
            with(api.getUnreadCounters(state.token)) {
                checkSuccessful()
                state.unreadPosts = this.posts
                state.unreadComments = this.comments
                state.privateUnreadPosts = this.privatePosts
                state.privateUnreadComments = this.privateComments
            }
        }
    }

    fun resetActivityBackStack() {
        val intent: Intent = if (state.isLoggedIn == true) {
            MainActivity.getIntent(baseContext)
        } else {
            LoginActivity.getIntent(baseContext)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}