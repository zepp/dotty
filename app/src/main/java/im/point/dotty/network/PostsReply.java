package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class PostsReply extends Envelope {
    @SerializedName("has next") boolean hasNext;
    List<MetaPost> posts;

    public List<MetaPost> getPosts() {
        return posts;
    }
}
