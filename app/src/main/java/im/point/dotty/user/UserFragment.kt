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
import im.point.dotty.common.AvatarOutline
import im.point.dotty.common.NavFragment
import im.point.dotty.common.RecyclerItemDecorator
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentUserBinding
import im.point.dotty.feed.FeedAdapter
import im.point.dotty.model.PostType
import im.point.dotty.model.UserPost
import im.point.dotty.post.PostFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@FlowPreview
class UserFragment : NavFragment<UserViewModel>() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var adapter: FeedAdapter<UserPost>
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        showSnackbar(exception.localizedMessage)
    }

    override fun provideViewModel(): UserViewModel {
        return requireArguments().let {
            ViewModelProvider(this, ViewModelFactory(requireActivity(),
                    it.getLong(USER_ID, -1), it.getString(USER_LOGIN)!!))
                    .get(UserViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        adapter = FeedAdapter(lifecycleScope, viewModel::getPostAvatar)
        adapter.onItemClicked = { post ->
            val bundle = Bundle()
            bundle.putString(PostFragment.POST_ID, post.id)
            bundle.putSerializable(PostFragment.POST_TYPE, PostType.USER_POST)
            findNavController().navigate(R.id.action_userFragment_to_postFragment, bundle)
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
                viewModel.fetchUserAndPosts().onCompletion { onFetched() }.collect()
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
                binding.userToolbar.title = user.formattedLogin
                binding.userName.visibility = if (user.name.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.userName.text = user.name
                binding.userAbout.visibility = if (user.about.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.userAbout.text = user.about
            }
        }

        launch(exceptionHandler) {
            viewModel.getUserAvatar().collect {
                binding.userAvatar.setImageBitmap(it)
            }
        }

        launch {
            viewModel.posts.collect { items -> adapter.list = items }
        }
    }

    private fun onFetched() {
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