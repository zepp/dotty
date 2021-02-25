package im.point.dotty.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.RxFragment
import im.point.dotty.common.TagsAdapter
import im.point.dotty.databinding.FragmentPostBinding
import im.point.dotty.domain.PostViewModel
import im.point.dotty.domain.ViewModelFactory
import im.point.dotty.model.Post

class PostFragment : RxFragment() {
    private val adapter: CommentAdapter = CommentAdapter()
    private lateinit var binding: FragmentPostBinding
    private lateinit var viewModel: PostViewModel
    private lateinit var layout: SwipeRefreshLayout
    private lateinit var postId: String
    private lateinit var from: From
    private val tagsAdapter: TagsAdapter = TagsAdapter()

    companion object {
        const val POST_ID = "post-id"
        const val POST_FROM = "post-from"

        fun newInstance(from: From, id: String): PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putSerializable(POST_FROM, from)
            args.putString(POST_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString(POST_ID)!!
        from = arguments?.get(POST_FROM) as From
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPostBinding.inflate(layoutInflater, container, false);
        binding.postComments.adapter = adapter
        binding.postTags.adapter = tagsAdapter
        binding.postTags.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout = binding.postSwipeLayout
        layout.setOnRefreshListener {
            addDisposable(when (from) {
                From.FROM_ALL -> viewModel.fetchAllPostComments(postId)
                From.FROM_COMMENTED -> viewModel.fetchCommentedPostComments(postId)
                From.FROM_RECENT -> viewModel.fetchRecentPostComments(postId)
            }.subscribe({ layout.isRefreshing = false },
                    { error ->
                        layout.isRefreshing = false
                        error.message?.let { showSnackbar(it) }
                    }))
        }
        adapter.onIdClicked = { id, pos -> binding.postComments.smoothScrollToPosition(pos) }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addDisposable(when (from) {
            From.FROM_ALL -> viewModel.fetchAllPostComments(postId)
            From.FROM_COMMENTED -> viewModel.fetchCommentedPostComments(postId)
            From.FROM_RECENT -> viewModel.fetchRecentPostComments(postId)
        }.subscribe({}, { error -> error.message?.let { showSnackbar(it) } }))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity()))
                .get(PostViewModel::class.java)
        addDisposable(when (from) {
            From.FROM_RECENT -> viewModel.getRecentPost(postId)
            From.FROM_COMMENTED -> viewModel.getCommentedPost(postId)
            From.FROM_ALL -> viewModel.getAllPost(postId)
        }.subscribe { post: Post ->
            binding.postText.text = post.text
            if (post.tags.isNullOrEmpty()) {
                binding.postTags.visibility = View.GONE
            } else {
                tagsAdapter.list = post.tags!!
            }
        })
        addDisposable(when (from) {
            From.FROM_ALL -> viewModel.getAllPostComments(postId)
            From.FROM_COMMENTED -> viewModel.getCommentedPostComments(postId)
            From.FROM_RECENT -> viewModel.getRecentPostComments(postId)
        }.subscribe { list -> adapter.list = list })
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(requireActivity().findViewById(R.id.post_layout), text, Snackbar.LENGTH_LONG).show()
    }
}