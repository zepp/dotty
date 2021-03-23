/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentFeedBinding
import im.point.dotty.main.MainViewModel
import im.point.dotty.model.Post
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class FeedFragment<T : Post> : Fragment() {
    protected lateinit var binding: FragmentFeedBinding
    protected lateinit var viewModel: MainViewModel
    protected lateinit var adapter: FeedAdapter<T>
    protected lateinit var feedPosts: RecyclerView
    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("error: ", exception.message, exception)
        finishUpdate()
        showSnackbar(exception.localizedMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory<Any>(requireActivity()))
                .get(MainViewModel::class.java)
        adapter = FeedAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedPosts = binding.feedPosts
        feedPosts.adapter = adapter
        feedPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val manager = feedPosts.layoutManager as LinearLayoutManager
            override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(view, dx, dy)
                if (view.scrollState == RecyclerView.SCROLL_STATE_SETTLING && dy > 0
                        && manager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                    binding.feedRefreshLayout.isRefreshing = true
                    onFeedUpdateBefore()
                }
            }
        })
        binding.feedRefreshLayout.setOnRefreshListener(this::onFeedUpdate)
    }

    protected fun finishUpdate() {
        binding.feedRefreshLayout.isRefreshing = false
    }

    protected abstract fun onFeedUpdate()

    protected abstract fun onFeedUpdateBefore()

    protected fun showSnackbar(text: String) {
        Snackbar.make(requireActivity().findViewById(R.id.main_layout), text, Snackbar.LENGTH_LONG).show()
    }
}