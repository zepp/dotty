package im.point.dotty.domain

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(application) as T
        } else if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            AuthViewModel(application) as T
        } else {
            throw IllegalArgumentException(modelClass.simpleName + " can not be constructed by this factory")
        }
    }

}