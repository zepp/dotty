package im.point.dotty.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.*
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
            val bundle = Bundle()
            bundle.putString(PostFragment.POST_ID, post.id)
            bundle.putLong(PostFragment.USER_ID, post.metapost.userId)
            bundle.putSerializable(PostFragment.POST_TYPE, PostType.USER_POST)
            findNavController().navigate(R.id.action_user_to_post, bundle)
        }
        adapter.onUserClicked = { id, login ->
            if (id != userId) {
                val bundle = Bundle()
                bundle.putLong(USER_ID, id)
                bundle.putString(USER_LOGIN, login)
                findNavController().navigate(R.id.action_user_fragment_self, bundle)
            }
        }
        adapter.onTagClicked = { tag ->
            val bundle = Bundle()
            bundle.putString(TagFragment.TAG, tag)
            findNavController().navigate(R.id.action_user_to_tag, bundle)
        }
        binding.userPosts.adapter = adapter
        binding.userPosts.addItemDecoration(RecyclerItemDecorator(requireContext(), DividerItemDecoration.VERTICAL, 4))
        binding.userAvatar.outlineProvider = AvatarOutline(64)
        binding.userAvatar.clipToOutline = true

        binding.userSubscribe.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onSubscribeChecked(isChecked).collect()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isSubscribed.collect { binding.userSubscribe.isChecked = it }
        }
        binding.userRecommendSubscribe.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onRecSubscribeChecked(isChecked).collect()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isRecSubscribed.collect { binding.userRecommendSubscribe.isChecked = it }
        }
        binding.userBlock.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onBlockChecked(isChecked).collect()
            }
        }
        lifecycleScope.launchWhenStarted {
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

    private fun bind() = lifecycleScope.launchWhenStarted {
        launch {
            viewModel.isActionsVisible.collect { value ->
                binding.userActions.visibility = if (value) View.VISIBLE else View.GONE
            }
        }

        launch {
            viewModel.user.collect { user ->
                binding.toolbar.title = user.formattedLogin
                binding.userName.visibility = if (user.name.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.userName.text = user.name
            }
        }

        launch {
            viewModel.getUserAvatar().collect {
                binding.userAvatar.setImageBitmap(it)
            }
        }

        launch {
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