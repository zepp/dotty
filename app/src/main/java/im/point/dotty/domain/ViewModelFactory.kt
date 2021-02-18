package im.point.dotty.domain

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.DottyApplication

class ViewModelFactory(activity: Activity) : ViewModelProvider.Factory {
    private val application: DottyApplication = activity.application as DottyApplication

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            mainViewModel as T
        } else if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            authViewModel as T
        } else if (modelClass.isAssignableFrom(postViewModel::class.java)) {
            postViewModel as T
        } else {
            throw IllegalArgumentException(modelClass.simpleName + " can not be constructed by this factory")
        }
    }

    private val mainViewModel by lazy {
        MainViewModel(application)
    }

    private val authViewModel by lazy {
        AuthViewModel(application)
    }

    private val postViewModel by lazy {
        PostViewModel(application)
    }
}