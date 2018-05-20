package im.point.dotty.network;

import java.util.List;

public final class PostReply extends Envelope {
    RawPost post;
    List<RawComment> comments;

    public RawPost getPost() {
        return post;
    }

    public List<RawComment> getComments() {
        return comments;
    }
}
