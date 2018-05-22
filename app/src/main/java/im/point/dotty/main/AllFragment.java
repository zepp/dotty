package im.point.dotty.main;

import android.widget.Toast;

import java.util.List;

import im.point.dotty.feed.FeedFragment;
import im.point.dotty.network.MetaPost;
import io.reactivex.observers.DisposableSingleObserver;

public final class AllFragment extends FeedFragment {
    public AllFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        interactor.fetchAll().subscribe(new DisposableSingleObserver<List<MetaPost>>() {
            @Override
            public void onSuccess(List<MetaPost> metaPosts) {
                adapter.setList(metaPosts);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
