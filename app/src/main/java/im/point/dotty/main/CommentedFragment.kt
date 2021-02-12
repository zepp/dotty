package im.point.dotty.main

import android.view.MenuItem
import android.widget.Toast
import im.point.dotty.R
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CommentedPost

class CommentedFragment : FeedFragment<CommentedPost>() {
    override fun onStart() {
        super.onStart()
        addDisposable(viewModel.getCommented().subscribe(
                {list -> adapter.list = list},
                {error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()}))
    }

    override fun onFeedUpdate() {
        addDisposable(viewModel.fetchCommented(false).subscribe(this::finishUpdate,
                {error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()}))
    }

    override fun onFeedUpdateBefore() {
        addDisposable(viewModel.fetchCommented(true).subscribe(this::finishUpdate,
                {error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()}))
    }
}