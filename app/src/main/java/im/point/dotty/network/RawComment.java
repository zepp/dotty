package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public class RawComment {
    String created;
    String text;
    Author author;
    @SerializedName("post id") String postId;
    @SerializedName("to comment id") int toCommentId;
    @SerializedName("is rec") boolean isRecommendation;
    int id;

    public String getCreated() {
        return created;
    }

    public String getText() {
        return text;
    }

    public Author getAuthor() {
        return author;
    }

    public String getPostId() {
        return postId;
    }

    public int getToCommentId() {
        return toCommentId;
    }

    public boolean isRecommendation() {
        return isRecommendation;
    }

    public int getId() {
        return id;
    }
}
