/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.NavFragment
import im.point.dotty.common.RecyclerItemDecorator
import im.point.dotty.common.TagsAdapter
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentPostBinding
import im.point.dotty.model.PostType
import im.point.dotty.tag.TagFragment
import im.point.dotty.user.UserFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@FlowPreview
class PostFragment : NavFragment<PostViewModel>() {
    private lateinit var adapter: CommentAdapter
    private lateinit var binding: FragmentPostBinding
    private lateinit var layout: SwipeRefreshLayout
    private val tagsAdapter: TagsAdapter = TagsAdapter()
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        layout.isRefreshing = false
        showSnackbar(exception.localizedMessage)
    }

    override fun provideViewModel(): PostViewModel {
        requireArguments().let {
            val type = it.getSerializable(POST_TYPE) as PostType
            val postId = it.getString(POST_ID)!!
            return if (type == PostType.TAGGED_POST) {
                val tag = it.getString(TAG)!!
                ViewModelProvider(this, ViewModelFactory(requireActivity(), type, postId, tag))
            } else {
                val userId = it.getLong(USER_ID)
                ViewModelProvider(this, ViewModelFactory(requireActivity(), type, postId, userId))
            }.get(PostViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.postTags.adapter = tagsAdapter
        binding.postTags.addItemDecoration(RecyclerItemDecorator(requireContext(), LinearLayoutManager.HORIZONTAL, 4))
        tagsAdapter.onTagClicked = { tag ->
            val bundle = Bundle()
            bundle.putString(TagFragment.TAG, tag)
            findNavController().navigate(R.id.action_post_to_tag, bundle)
        }
        adapter = CommentAdapter(lifecycleScope)
        binding.postComments.adapter = adapter
        adapter.avatarProvider = viewModel::getAvatar
        adapter.onIdClicked = { _, pos -> binding.postComments.smoothScrollToPosition(pos) }
        adapter.onUserClicked = { id, login ->
            val bundle = Bundle()
            bundle.putLong(UserFragment.USER_ID, id)
            bundle.putString(UserFragment.USER_LOGIN, login)
            findNavController().navigate(R.id.action_post_to_user, bundle)
        }
        layout = binding.postSwipeLayout
        layout.setOnRefreshListener {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.fetchPostComments().onCompletion { layout.isRefreshing = false }.collect()
            }
        }
        bind()
        return binding.root
    }

    private fun bind() {
        lifecycleScope.launchWhenStarted {
            binding.postSubscribe.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onSubscribeChecked(isChecked).collect { } }
            }
            viewModel.isSubscribed.collect { binding.postSubscribe.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.postRecommend.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onRecommendChecked(isChecked).collect() }
            }
            viewModel.isRecommended.collect { binding.postRecommend.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.postBookmark.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onBookmarkChecked(isChecked).collect() }
            }
            viewModel.isBookmarked.collect { binding.postBookmark.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.postPin.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onPinChecked(isChecked).collect() }
            }
            viewModel.isPinned.collect { binding.postPin.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isPinVisible.collect {
                binding.postPin.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.post.collect { post ->
                binding.toolbar.title = post.formattedLogin
                binding.postText.text = post.text
                binding.postTags.visibility = if (post.tags.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                tagsAdapter.list = post.tags
            }
        }

        lifecycleScope.launch(exceptionHandler) {
            viewModel.comments.collect { list -> adapter.list = list }
        }
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(requireActivity().findViewById(R.id.post_layout), text, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val POST_ID = "post-id"
        const val POST_TYPE = "post-type"
        const val USER_ID = "user-id"
        const val TAG = "tag"
    }
}