package im.point.dotty.main

import android.widget.Toast
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.RecentPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity

class RecentFragment : FeedFragment<RecentPost>() {
    override fun onStart() {
        super.onStart()
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(this.requireContext(), From.FROM_RECENT, post.id))
        }
        addDisposable(viewModel.getRecent().subscribe(
                { list -> adapter.list = list },
                { error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show() }))
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