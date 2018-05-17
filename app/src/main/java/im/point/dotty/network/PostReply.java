package im.point.dotty.network;

import java.util.List;

public final class PostReply extends Envelope {
    Post post;
    List<Comment> comments;

    public Post getPost() {
        return post;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
