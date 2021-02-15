package im.point.dotty.main

import android.view.MenuItem
import android.widget.Toast
import im.point.dotty.R
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.RecentPost

class RecentFragment : FeedFragment<RecentPost>() {
    override fun onStart() {
        super.onStart()
        addDisposable(viewModel.getRecent().subscribe(
                {list -> adapter.list = list},
                {error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()}))
    }

    override fun onFeedUpdate() {
        addDisposable(viewModel.fetchRecent(false).subscribe(this::finishUpdate)
        { error -> finishUpdate()
            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()})
    }

    override fun onFeedUpdateBefore() {
        addDisposable(viewModel.fetchRecent(true).subscribe(this::finishUpdate)
        { error -> finishUpdate()
            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()})
    }
}