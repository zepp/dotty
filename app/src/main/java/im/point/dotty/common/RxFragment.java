package im.point.dotty.common;

import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxFragment extends Fragment {
    private final CompositeDisposable composite = new CompositeDisposable();

    protected final void addDisposable(Disposable disposable) {
        composite.add(disposable);
    }

    @Override
    public void onStop() {
        super.onStop();
        composite.clear();
    }
}
