package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class MetaPost {
    @SerializedName("bookmarked") boolean isBookmarked;
    long uid;
    @SerializedName("subscribed") boolean isSubscribed;
    @SerializedName("editable") boolean isEditable;
    @SerializedName("recommended") boolean isRecommended;
    Post post;

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public long getUid() {
        return uid;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public Post getPost() {
        return post;
    }
}
