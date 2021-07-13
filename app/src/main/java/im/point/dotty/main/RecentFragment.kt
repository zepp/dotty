/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import im.point.dotty.R
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CompleteRecentPost
import im.point.dotty.model.PostType
import im.point.dotty.post.PostFragment
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@FlowPreview
class RecentFragment : FeedFragment<MainViewModel, CompleteRecentPost>() {

    override fun provideViewModel(): MainViewModel =
            ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity()))
                    .get(MainViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        adapter.avatarProvider = viewModel::getAvatar
        adapter.imagesProvider = viewModel::getPostImages
        adapter.onPostClicked = { post ->
            val bundle = Bundle()
            bundle.putString(PostFragment.POST_ID, post.id)
            bundle.putSerializable(PostFragment.POST_TYPE, PostType.RECENT_POST)
            findNavController().navigate(R.id.action_main_to_post, bundle)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.recent.collect { adapter.list = it }
        }
    }

    override fun onFeedUpdate() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchRecent(false).onCompletion { finishUpdate() }.collect()
            viewModel.fetchUnreadCounters().await()
        }
    }

    override fun onFeedUpdateBefore() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchRecent(true).onCompletion { finishUpdate() }.collect()
        }
    }
}