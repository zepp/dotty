package im.point.dotty.feed;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import im.point.dotty.R;
import im.point.dotty.network.MetaPost;

class PostHolder extends RecyclerView.ViewHolder {
    private TextView tags;
    private TextView id;
    private TextView text;

    PostHolder(View itemView) {
        super(itemView);
        tags = itemView.findViewById(R.id.post_tags);
        id = itemView.findViewById(R.id.post_id);
        text = itemView.findViewById(R.id.post_text);
    }

    String join(List<String> list, String delim) {
        String a = "";
        for (String tag : list) {
            a += " " + tag;
        }
        return a.trim();
    }

    void bind(MetaPost post) {
        tags.setText(join(post.getPost().getTags(), " "));
        id.setText(post.getPost().getId());
        text.setText(post.getPost().getText());
    }
}
