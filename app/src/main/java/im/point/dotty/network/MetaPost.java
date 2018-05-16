package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class MetaPost {
    @SerializedName("bookmarked") boolean isBookmarked;
    long uid;
    @SerializedName("subscribed") boolean isSubscribed;
    @SerializedName("editable") boolean isEditable;
    @SerializedName("recommended") boolean isRecommended;
    Post post;
}
