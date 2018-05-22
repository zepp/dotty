package im.point.dotty.feed;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.point.dotty.R;
import im.point.dotty.domain.InteractorManager;
import im.point.dotty.domain.MainInteractor;
import im.point.dotty.model.Post;

public abstract class FeedFragment<T extends Post> extends Fragment {
    protected RecyclerView posts;
    protected MainInteractor interactor;
    protected FeedAdapter<T> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactor = InteractorManager.from(this).get(MainInteractor.class);
        adapter = new FeedAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posts = view.findViewById(R.id.feed_posts);
        posts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        posts.setAdapter(adapter);
    }
}
