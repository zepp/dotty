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
import im.point.dotty.user.UserViewModel

class ViewModelFactory<P>(activity: Activity, vararg va: P) : ViewModelProvider.Factory {
    private val application: DottyApplication = activity.application as DottyApplication
    private val args: Array<out P> = va

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(application) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(application) as T
        } else if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            PostViewModel(application) as T
        } else if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            if (args.size != 1) throw Exception("user ID is not supplied")
            UserViewModel(application, args.get(0) as Long) as T
        } else {
            throw IllegalArgumentException(modelClass.simpleName + " can not be constructed by this factory")
        }
    }
}