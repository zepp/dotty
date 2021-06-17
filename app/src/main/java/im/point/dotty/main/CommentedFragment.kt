/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.PostType
import im.point.dotty.post.PostActivity
import im.point.dotty.user.UserActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentedFragment : FeedFragment<CommentedPost>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(requireContext(), PostType.COMMENTED_POST, post.id))
        }
        adapter.onUserClicked = { id, login ->
            startActivity(UserActivity.getIntent(requireContext(), id, login))
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