package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class CountersReply extends Envelope {
    int posts;
    int comments;
    @SerializedName("private posts") int privatePosts;
    @SerializedName("private_comments") int privateComments;

    public int getPosts() {
        return posts;
    }

    public int getComments() {
        return comments;
    }

    public int getPrivatePosts() {
        return privatePosts;
    }

    public int getPrivateComments() {
        return privateComments;
    }
}
