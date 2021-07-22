package im.point.dotty.tag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.*
import im.point.dotty.databinding.FragmentTagBinding
import im.point.dotty.feed.FeedAdapter
import im.point.dotty.model.CompleteTaggedPost
import im.point.dotty.model.PostType
import im.point.dotty.post.PostFragment
import im.point.dotty.user.UserFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@FlowPreview
class TagFragment : NavFragment<TaggedPostViewModel>() {
    private lateinit var binding: FragmentTagBinding
    private lateinit var adapter: FeedAdapter<CompleteTaggedPost>
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        finishRefreshing()
        showSnackbar(exception.localizedMessage)
    }

    private val myTag by lazy {
        requireArguments().getString(TAG)!!
    }

    override fun provideViewModel() =
            ViewModelProvider(this, ViewModelFactory(requireActivity(), myTag))
                    .get(TaggedPostViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTagBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = FeedAdapter(lifecycleScope)
        adapter.avatarProvider = viewModel::getPostAvatar
        adapter.imagesProvider = viewModel::getPostImages
        adapter.onPostClicked = { post ->
            val bundle = Bundle()
            bundle.putString(PostFragment.POST_ID, post.id)
            bundle.putSerializable(PostFragment.POST_TYPE, PostType.TAGGED_POST)
            bundle.putString(PostFragment.TAG, myTag)
            findNavController().navigate(R.id.action_tag_to_post, bundle)
        }
        adapter.onUserClicked = { id, login ->
            val bundle = Bundle()
            bundle.putLong(UserFragment.USER_ID, id)
            bundle.putString(UserFragment.USER_LOGIN, login)
            findNavController().navigate(R.id.action_tag_to_user, bundle)
        }
        adapter.onTagClicked = { tag ->
            if (tag != myTag) {
                val bundle = Bundle()
                bundle.putString(TAG, tag)
                findNavController().navigate(R.id.action_tag_to_tag, bundle)
            }
        }

        with(binding.tagPosts) {
            adapter = this@TagFragment.adapter

            addOnLastItemDisplayedListener {
                lifecycleScope.launch(exceptionHandler) {
                    viewModel.fetch(true).onCompletion { finishRefreshing() }.collect()
                }
            }
        }

        binding.tagRefresh.setOnRefreshListener {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.fetch().onCompletion { finishRefreshing() }.collect()
            }
        }

        bind()

        return binding.root
    }

    private fun bind() {
        binding.tagToolbar.title = "#$myTag"
        lifecycleScope.launchWhenStarted {
            viewModel.posts.collect {
                adapter.list = it
            }
        }
    }

    private fun finishRefreshing() {
        binding.tagRefresh.isRefreshing = false
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(requireActivity().findViewById(R.id.tag_layout), text, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val TAG = "tag"
    }
}