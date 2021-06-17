/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import im.point.dotty.R
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.PostType
import im.point.dotty.post.PostFragment
import im.point.dotty.user.UserFragment
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
class CommentedFragment : FeedFragment<CommentedPost>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.onItemClicked = { post ->
            val bundle = Bundle()
            bundle.putString(PostFragment.POST_ID, post.id)
            bundle.putSerializable(PostFragment.POST_TYPE, PostType.COMMENTED_POST)
            findNavController().navigate(R.id.action_main_fragment_to_postFragment, bundle)
        }
        adapter.onUserClicked = { id, login ->
            val bundle = Bundle()
            bundle.putLong(UserFragment.USER_ID, id)
            bundle.putString(UserFragment.USER_LOGIN, login)
            findNavController().navigate(R.id.action_main_fragment_to_userFragment, bundle)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.commented.collect { list -> adapter.list = list }
        }
    }

    override fun onFeedUpdate() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchCommented(false).collect { finishUpdate() }
        }
    }

    override fun onFeedUpdateBefore() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchCommented(true).collect { finishUpdate() }
        }
    }
}