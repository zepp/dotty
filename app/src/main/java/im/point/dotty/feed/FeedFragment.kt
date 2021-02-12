package im.point.dotty.feed

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.common.RxFragment
import im.point.dotty.databinding.FragmentFeedBinding
import im.point.dotty.domain.MainViewModel
import im.point.dotty.domain.ViewModelFactory
import im.point.dotty.model.Post

abstract class FeedFragment<T : Post> : RxFragment() {
    protected lateinit var binding: FragmentFeedBinding
    protected lateinit var viewModel: MainViewModel
    protected lateinit var adapter: FeedAdapter<T>
    protected lateinit var feedPosts : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
                ViewModelFactory(requireActivity())).get(MainViewModel::class.java)
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
}