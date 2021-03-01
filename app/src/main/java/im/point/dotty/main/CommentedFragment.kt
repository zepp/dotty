/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CommentedPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity
import im.point.dotty.user.UserActivity

class CommentedFragment : FeedFragment<CommentedPost>() {
    override fun onStart() {
        super.onStart()
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(requireContext(), From.FROM_COMMENTED, post.id))
        }
        adapter.onUserClicked = { id ->
            startActivity(UserActivity.getIntent(requireContext(), id))
        }
        addDisposable(viewModel.getCommented().subscribe(
                { list -> adapter.list = list },
                { error -> error.message?.let { showSnackbar(it) } }))
    }

    override fun onFeedUpdate() {
        addDisposable(viewModel.fetchCommented(false).subscribe(this::finishUpdate)
        { error ->
            finishUpdate()
            error.message?.let { showSnackbar(it) }
        })
    }

    override fun onFeedUpdateBefore() {
        addDisposable(viewModel.fetchCommented(true).subscribe(this::finishUpdate)
        { error ->
            finishUpdate()
            error.message?.let { showSnackbar(it) }
        })
    }
}