/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import im.point.dotty.R
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.common.repeatOnStarted
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CompleteAllPost
import im.point.dotty.model.PostType
import im.point.dotty.post.PostFragment
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@FlowPreview
class AllFragment : FeedFragment<MainViewModel, CompleteAllPost>() {
    override fun provideViewModel(): MainViewModel =
            ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity()))
                    .get(MainViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        adapter.avatarProvider = viewModel::getAvatar
        adapter.imagesProvider = viewModel::getPostImages
        adapter.onPostClicked = { post ->
            findNavController().navigate(R.id.action_main_to_post,
                    bundleOf(PostFragment.POST_ID to post.id,
                            PostFragment.POST_TYPE to PostType.ALL_POST))
        }
        repeatOnStarted {
            viewModel.all.collect { adapter.list = it }
        }
    }

    override fun onFeedUpdate() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchAll(false).onCompletion { finishUpdate() }.collect()
        }
    }

    override fun onFeedUpdateBefore() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchAll(true).onCompletion { finishUpdate() }.collect()
        }
    }
}