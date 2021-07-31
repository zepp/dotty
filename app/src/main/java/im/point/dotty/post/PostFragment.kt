/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@FlowPreview
class PostFragment : NavFragment<PostViewModel>() {
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var bitmapAdapter: BitmapAdapter
    private lateinit var binding: FragmentPostBinding
    private lateinit var layout: SwipeRefreshLayout
    private val tagsAdapter: TagsAdapter = TagsAdapter(R.layout.list_item_inverted_tag)
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
            findNavController().navigate(R.id.action_post_to_tag, bundleOf(TagFragment.TAG to tag))
        }
        commentAdapter = CommentAdapter(lifecycleScope)
        binding.postComments.adapter = commentAdapter
        commentAdapter.avatarProvider = viewModel::getAvatar
        commentAdapter.onIdClicked = { _, pos -> binding.postComments.smoothScrollToPosition(pos) }
        commentAdapter.onUserClicked = { id, login ->
            findNavController().navigate(R.id.action_post_to_user,
                    bundleOf(UserFragment.USER_ID to id,
                            UserFragment.USER_LOGIN to login))
        }
        bitmapAdapter = BitmapAdapter()
        binding.postFiles.adapter = bitmapAdapter
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
        binding.postSubscribe.onCheckedChangeListener = { isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onSubscribeChecked(isChecked).collect()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isSubscribed.collect { binding.postSubscribe.isChecked = it }
        }
        binding.postRecommend.onCheckedChangeListener = { isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                viewModel.onRecommendChecked(isChecked).collect()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isRecommended.collect { binding.postRecommend.isChecked = it }
        }
        binding.postBookmark.onCheckedChangeListener = { isChecked ->
            lifecycleScope.launch(exceptionHandler) { viewModel.onBookmarkChecked(isChecked).collect() }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isBookmarked.collect { binding.postBookmark.isChecked = it }
        }
        binding.postPin.onCheckedChangeListener = { isChecked ->
            lifecycleScope.launch(exceptionHandler) { viewModel.onPinChecked(isChecked).collect() }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isPinned.collect { binding.postPin.isChecked = it }
        }
        binding.postRemove.setOnClickListener { view ->
            lifecycleScope.launch(exceptionHandler) {
                view.isEnabled = false
                viewModel.onPostRemove()
                        .catch {
                            view.isEnabled = true
                            throw it
                        }.collect {
                            findNavController().popBackStack()
                        }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isPinned.collect { binding.postPin.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isUserPost.collect {
                binding.postPin.visibility = if (it) View.VISIBLE else View.GONE
                binding.postRemove.visibility = if (it) View.VISIBLE else View.GONE
                binding.postRecommend.visibility = if (it) View.GONE else View.VISIBLE
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.files.collect {
                binding.postFiles.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
                bitmapAdapter.list = it
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.post.collect { post ->
                binding.toolbar.title = post.formattedId
                binding.postText.text = post.text
                binding.postText.visibility = if (post.text.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                tagsAdapter.list = post.tags
                binding.postTags.visibility = if (post.tags.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }

        lifecycleScope.launch(exceptionHandler) {
            viewModel.comments.collect { commentAdapter.list = it }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isClosing.collect {
                if (it) {
                    findNavController().popBackStack()
                }
            }
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