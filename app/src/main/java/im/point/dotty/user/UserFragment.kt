package im.point.dotty.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.NavFragment
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.common.addOnLastItemDisplayedListener
import im.point.dotty.common.repeatOnStarted
import im.point.dotty.databinding.FragmentUserBinding
import im.point.dotty.feed.FeedAdapter
import im.point.dotty.model.CompleteUserPost
import im.point.dotty.model.PostType
import im.point.dotty.post.PostFragment
import im.point.dotty.tag.TagFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@FlowPreview
class UserFragment : NavFragment<UserViewModel>() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var adapter: FeedAdapter<CompleteUserPost>
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        showSnackbar(exception.localizedMessage)
    }
    private val userId by lazy { requireArguments().getLong(USER_ID, -1) }

    private val userLogin by lazy { requireArguments().getString(USER_LOGIN)!! }

    override fun provideViewModel() =
            ViewModelProvider(this, ViewModelFactory(requireActivity(),
                    userId, userLogin))
                    .get(UserViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        adapter = FeedAdapter(lifecycleScope)
        adapter.avatarProvider = viewModel::getPostAvatar
        adapter.imagesProvider = viewModel::getPostImages
        adapter.onPostClicked = { post ->
            findNavController().navigate(R.id.action_user_to_post,
                    bundleOf(PostFragment.POST_ID to post.id,
                            PostFragment.USER_ID to post.metapost.userId,
                            PostFragment.POST_TYPE to PostType.USER_POST))
        }
        adapter.onUserClicked = { id, login ->
            if (id != userId) {
                findNavController().navigate(R.id.action_user_fragment_self,
                        bundleOf(USER_ID to id,
                                USER_LOGIN to login))
            }
        }
        adapter.onTagClicked = { tag ->
            findNavController().navigate(R.id.action_user_to_tag,
                    bundleOf(TagFragment.TAG to tag))
        }
        binding.userPosts.adapter = adapter
        binding.userAvatar.clipToOutline = true

        binding.userSubscribe.onCheckedChangeListener = { isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onSubscribeChecked(isChecked).collect()
            }
        }
        binding.userRecommendSubscribe.onCheckedChangeListener = { isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onRecSubscribeChecked(isChecked).collect()
            }
        }
        binding.userBlock.onCheckedChangeListener = { isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onBlockChecked(isChecked).collect()
            }
        }
        repeatOnStarted {
            viewModel.isSubscribed.collect { binding.userSubscribe.isChecked = it }
        }
        repeatOnStarted {
            viewModel.isRecSubscribed.collect { binding.userRecommendSubscribe.isChecked = it }
        }
        repeatOnStarted {
            viewModel.isBlocked.collect { binding.userBlock.isChecked = it }
        }
        binding.userRefresh.setOnRefreshListener {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.fetchUserAndPosts().onCompletion { finishRefreshing() }.collect()
            }
        }
        binding.userPosts.addOnLastItemDisplayedListener {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.fetchBefore().collect()
            }
        }

        bind()
        return binding.root
    }

    private fun bind() = lifecycleScope.launch {
        repeatOnStarted {
            viewModel.user.collect { user ->
                binding.toolbar.title = user.formattedLogin
                binding.userName.visibility = if (user.name.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                binding.userName.text = user.name
            }
        }

        repeatOnStarted {
            viewModel.getUserAvatar().collect {
                binding.userAvatar.setImageBitmap(it)
            }
        }

        repeatOnStarted {
            viewModel.posts.collect { items -> adapter.list = items }
        }
    }

    private fun finishRefreshing() {
        binding.userRefresh.isRefreshing = false
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val USER_ID = "user-id"
        const val USER_LOGIN = "user-login"
    }
}