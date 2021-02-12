package im.point.dotty.main

import android.view.MenuItem
import android.widget.Toast
import im.point.dotty.R
import im.point.dotty.feed.FeedFragment
import im.point.dotty.model.CommentedPost
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber

class CommentedFragment : FeedFragment<CommentedPost>() {
    override fun onStart() {
        super.onStart()
        addDisposable(viewModel.getCommented().subscribe(
                {list -> adapter.list = list},
                {error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()}))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.main_refresh) {
            addDisposable(viewModel.fetchCommented().subscribe({},
                    {error -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()}))
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}