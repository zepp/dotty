package im.point.dotty.domain

import android.app.Application
import androidx.lifecycle.ViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.repository.RepoFactory

class PostViewModel(application: DottyApplication) : ViewModel() {
    private val repoFactory: RepoFactory

    init {
        repoFactory = application.repoFactory
    }
}