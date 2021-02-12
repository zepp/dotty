package im.point.dotty.feed

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.common.RxFragment
import im.point.dotty.databinding.FragmentFeedBinding
import im.point.dotty.domain.MainViewModel
import im.point.dotty.domain.ViewModelFactory
import im.point.dotty.model.Post

abstract class FeedFragment<T : Post> : RxFragment() {
    protected lateinit var binding: FragmentFeedBinding
    protected lateinit var viewModel: MainViewModel
    protected lateinit var adapter: FeedAdapter<T>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        binding.feedPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.feedPosts.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }
}