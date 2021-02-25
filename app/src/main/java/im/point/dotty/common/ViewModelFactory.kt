/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.DottyApplication
import im.point.dotty.login.LoginViewModel
import im.point.dotty.main.MainViewModel
import im.point.dotty.post.PostViewModel

class ViewModelFactory(activity: Activity) : ViewModelProvider.Factory {
    private val application: DottyApplication = activity.application as DottyApplication

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(application) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(application) as T
        } else if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            PostViewModel(application) as T
        } else {
            throw IllegalArgumentException(modelClass.simpleName + " can not be constructed by this factory")
        }
    }
}