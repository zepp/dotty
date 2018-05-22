package im.point.dotty.feed;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import im.point.dotty.R;
import im.point.dotty.model.Post;

class PostHolder extends RecyclerView.ViewHolder {
    private TextView author;
    private TextView id;
    private TextView text;
    private TextView tags;

    PostHolder(View itemView) {
        super(itemView);
        author = itemView.findViewById(R.id.post_author);
        id = itemView.findViewById(R.id.post_id);
        text = itemView.findViewById(R.id.post_text);
        tags = itemView.findViewById(R.id.post_tags);
    }

    void bind(Post post) {
        author.setText(post.name.length() == 0 ? post.login : post.name);
        id.setText("#" + post.textId);
        text.setText(post.text);
    }
}
