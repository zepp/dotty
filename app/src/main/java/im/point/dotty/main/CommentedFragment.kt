package im.point.dotty.main

import android.widget.Toast
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CommentedPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity

class CommentedFragment : FeedFragment<CommentedPost>() {
    override fun onStart() {
        super.onStart()
        adapter.onItemClicked = { post ->
            startActivity(PostActivity.getIntent(this.requireContext(), From.FROM_COMMENTED, post.id))
        }
        addDisposable(viewModel.getCommented().subscribe(
                { list -> adapter.list = list },
                { error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show() }))
    }

    override fun onFeedUpdate() {
        addDisposable(viewModel.fetchCommented(false).subscribe(this::finishUpdate)
        { error -> finishUpdate()
            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()})
    }

    override fun onFeedUpdateBefore() {
        addDisposable(viewModel.fetchCommented(true).subscribe(this::finishUpdate)
        { error -> finishUpdate()
            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()})
    }
}