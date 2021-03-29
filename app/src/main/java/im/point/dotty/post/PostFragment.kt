/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.TagsAdapter
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentPostBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostFragment : Fragment() {
    private val adapter: CommentAdapter = CommentAdapter()
    private lateinit var binding: FragmentPostBinding
    private lateinit var viewModel: PostViewModel
    private lateinit var layout: SwipeRefreshLayout
    private val tagsAdapter: TagsAdapter = TagsAdapter()
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        layout.isRefreshing = false
        showSnackbar(exception.localizedMessage)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPostBinding.inflate(layoutInflater, container, false);
        binding.postComments.adapter = adapter
        binding.postTags.adapter = tagsAdapter
        binding.postTags.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout = binding.postSwipeLayout
        layout.setOnRefreshListener {
            fetchPostComments()
        }
        adapter.onIdClicked = { id, pos -> binding.postComments.smoothScrollToPosition(pos) }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        fetchPostComments()
    }

    private fun fetchPostComments() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchPostComments().collect { layout.isRefreshing = false }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory<Any>(requireActivity()))
                .get(PostViewModel::class.java)
        lifecycleScope.launch(exceptionHandler) {
            viewModel.getPost().collect { post ->
                binding.postText.text = post.text
                if (post.tags.isNullOrEmpty()) {
                    binding.postTags.visibility = View.GONE
                } else {
                    tagsAdapter.list = post.tags!!
                }
            }
        }
        lifecycleScope.launch(exceptionHandler) {
            viewModel.getPostComments().collect { list -> adapter.list = list }
        }
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(requireActivity().findViewById(R.id.post_layout), text, Snackbar.LENGTH_LONG).show()
    }
}