/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.*
import im.point.dotty.databinding.FragmentFeedBinding
import im.point.dotty.model.CompletePost
import im.point.dotty.tag.TagFragment
import im.point.dotty.user.UserFragment
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class FeedFragment<M : DottyViewModel, T : CompletePost<*>> : NavFragment<M>() {
    protected lateinit var binding: FragmentFeedBinding
    protected lateinit var adapter: FeedAdapter<T>
    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        finishUpdate()
        showSnackbar(exception.localizedMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = FeedAdapter(lifecycleScope)
        adapter.onUserClicked = { id, login ->
            findNavController().navigate(R.id.action_main_to_user,
                    bundleOf(UserFragment.USER_ID to id,
                            UserFragment.USER_LOGIN to login))
        }
        adapter.onTagClicked = { tag ->
            findNavController().navigate(R.id.action_main_to_tag, bundleOf(TagFragment.TAG to tag))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.feedPosts) {
            adapter = this@FeedFragment.adapter
            addOnLastItemDisplayedListener { onFeedUpdateBefore() }
        }
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