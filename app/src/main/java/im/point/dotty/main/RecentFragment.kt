/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.RecentPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity

class RecentFragment : FeedFragment<RecentPost>() {
    override fun onStart() {
        super.onStart()
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(requireContext(), From.FROM_RECENT, post.id))
        }
        addDisposable(viewModel.getRecent().subscribe(
                { list -> adapter.list = list },
                { error -> error.message?.let { showSnackbar(it) } }))
    }

    override fun onFeedUpdate() {
        addDisposable(viewModel.fetchRecent(false).subscribe(this::finishUpdate)
        { error ->
            finishUpdate()
            error.message?.let { showSnackbar(it) }
        })
    }

    override fun onFeedUpdateBefore() {
        addDisposable(viewModel.fetchRecent(true).subscribe(this::finishUpdate)
        { error ->
            finishUpdate()
            error.message?.let { showSnackbar(it) }
        })
    }
}