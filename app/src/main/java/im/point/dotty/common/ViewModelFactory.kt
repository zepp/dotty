/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.DottyApplication

class ViewModelFactory(activity: Activity, private vararg val args: Any) : ViewModelProvider.Factory {
    private val application: DottyApplication = activity.application as DottyApplication

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = modelClass.getDeclaredConstructor(
                DottyApplication::class.java,
                Array<out Any>::class.java).newInstance(application, args)
        return viewModel as T
    }
}