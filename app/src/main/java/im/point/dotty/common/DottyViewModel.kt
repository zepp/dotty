/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.login.LoginActivity
import im.point.dotty.main.MainActivity
import im.point.dotty.network.AuthAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class DottyViewModel(application: DottyApplication) : AndroidViewModel(application) {
    protected val state = application.state
    protected val authAPI: AuthAPI = application.authApi

    suspend fun resetActivityBackStack() = withContext(Dispatchers.Main) {
        val intent: Intent = if (state.isLoggedIn == true) {
            MainActivity.getIntent(getApplication())
        } else {
            LoginActivity.getIntent(getApplication())
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        getApplication<Application>().startActivity(intent)
    }
}