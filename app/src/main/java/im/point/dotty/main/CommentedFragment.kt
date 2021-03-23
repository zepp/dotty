/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import androidx.lifecycle.lifecycleScope
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CommentedPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity
import im.point.dotty.user.UserActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentedFragment : FeedFragment<CommentedPost>() {
    override fun onStart() {
        super.onStart()
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(requireContext(), From.FROM_COMMENTED, post.id))
        }
        adapter.onUserClicked = { id ->
            startActivity(UserActivity.getIntent(requireContext(), id))
        }
        lifecycleScope.launch(exceptionHandler) {
            viewModel.getCommented().collect { list -> adapter.list = list }
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