package im.point.dotty.domain

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.DottyApplication

class ViewModelFactory(activity: Activity) : ViewModelProvider.Factory {
    private val application: DottyApplication = activity.application as DottyApplication

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(application) as T
        } else if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            AuthViewModel(application) as T
        } else if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            PostViewModel(application) as T
        }else {
            throw IllegalArgumentException(modelClass.simpleName + " can not be constructed by this factory")
        }
    }

}