package im.point.dotty.main;

import android.widget.Toast;

import java.util.List;

import im.point.dotty.feed.FeedFragment;
import im.point.dotty.model.AllPost;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

public final class AllFragment extends FeedFragment<AllPost> {
    public AllFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        interactor.getAll().subscribe(new DisposableSubscriber<List<AllPost>>() {
            @Override
            public void onNext(List<AllPost> allPosts) {
                adapter.setList(allPosts);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });
        interactor.fetchAll().subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
