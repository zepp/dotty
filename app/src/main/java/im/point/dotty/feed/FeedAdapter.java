package im.point.dotty.feed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import im.point.dotty.R;
import im.point.dotty.network.MetaPost;

public class FeedAdapter extends RecyclerView.Adapter<PostHolder> {
    private List<MetaPost> list;

    FeedAdapter() {
        setHasStableIds(true);
        this.list = new ArrayList<>();
    }

    public List<MetaPost> getList() {
        return list;
    }

    public void setList(List<MetaPost> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getUid();
    }
}
