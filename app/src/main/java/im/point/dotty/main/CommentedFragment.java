package im.point.dotty.main;

import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import im.point.dotty.R;
import im.point.dotty.feed.FeedFragment;
import im.point.dotty.model.CommentedPost;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

public final class CommentedFragment extends FeedFragment<CommentedPost> {
    public CommentedFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        interactor.getCommented().subscribe(new DisposableSubscriber<List<CommentedPost>>() {
            @Override
            public void onNext(List<CommentedPost> commentedPosts) {
                adapter.setList(commentedPosts);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_refresh) {
            interactor.fetchCommented().subscribe(new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
