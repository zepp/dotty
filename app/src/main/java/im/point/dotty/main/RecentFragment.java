package im.point.dotty.main;

import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import im.point.dotty.R;
import im.point.dotty.feed.FeedFragment;
import im.point.dotty.model.RecentPost;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

public final class RecentFragment extends FeedFragment<RecentPost> {
    public RecentFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        interactor.getRecent().subscribe(new DisposableSubscriber<List<RecentPost>>() {
            @Override
            public void onNext(List<RecentPost> recentPosts) {
                adapter.setList(recentPosts);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_refresh) {
            interactor.fetchRecent().subscribe(new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
