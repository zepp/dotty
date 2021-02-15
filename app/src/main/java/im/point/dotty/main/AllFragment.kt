package im.point.dotty.main

import android.widget.Toast
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.AllPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity

class AllFragment : FeedFragment<AllPost>() {
    override fun onStart() {
        super.onStart()
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(this.requireContext(), From.FROM_ALL, post.id))
        }
        addDisposable(viewModel.getAll().subscribe(
                { list -> adapter.list = list },
                { error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show() }))
    }

    override fun onFeedUpdate() {
        addDisposable(viewModel.fetchAll(false).subscribe(this::finishUpdate)
        { error -> finishUpdate()
            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()})
    }

    override fun onFeedUpdateBefore() {
        addDisposable(viewModel.fetchAll(true).subscribe(this::finishUpdate)
        { error -> finishUpdate()
            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()})
    }
}