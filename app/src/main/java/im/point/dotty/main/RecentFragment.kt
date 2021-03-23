/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import androidx.lifecycle.lifecycleScope
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.RecentPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity
import im.point.dotty.user.UserActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecentFragment : FeedFragment<RecentPost>() {
    override fun onStart() {
        super.onStart()
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(requireContext(), From.FROM_RECENT, post.id))
        }
        adapter.onUserClicked = { id ->
            startActivity(UserActivity.getIntent(requireContext(), id))
        }
        lifecycleScope.launch(exceptionHandler) {
            viewModel.getRecent().collect { list ->
                adapter.list = list
                finishUpdate()
            }
        }
    }

    override fun onFeedUpdate() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchRecent(false).collect { finishUpdate() }
        }
    }

    override fun onFeedUpdateBefore() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchRecent(true).collect { finishUpdate() }
        }
    }
}