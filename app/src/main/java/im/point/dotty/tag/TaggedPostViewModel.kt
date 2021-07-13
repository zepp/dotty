package im.point.dotty.tag

import androidx.lifecycle.viewModelScope
import im.point.dotty.DottyApplication
import im.point.dotty.common.DottyViewModel
import im.point.dotty.repository.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaggedPostViewModel(app: DottyApplication, vararg args: Any) : DottyViewModel(app) {
    private val tag = args[0] as String

    private val repo = app.repoFactory.getTaggedPostRepo(tag)
    private val avaRepo = app.avaRepo
    private val fileRepository = app.postFilesRepo

    val posts = repo.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun getPostAvatar(login: String) = avaRepo.getAvatar(login, Size.SIZE_80)

    fun getPostImages(postId: String) = fileRepository.getPostFiles(postId)

    fun fetch(isBefore: Boolean = false) =
            if (isBefore) repo.fetchBefore().flowOn(Dispatchers.IO)
            else repo.fetchAll().flowOn(Dispatchers.IO)

    init {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            repo.fetchAll().collect()
        }
    }
}