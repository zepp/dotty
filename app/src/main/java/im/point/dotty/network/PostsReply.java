package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class PostsReply {
    @SerializedName("has next") boolean hasNext;
    MetaPost[] posts;
}
