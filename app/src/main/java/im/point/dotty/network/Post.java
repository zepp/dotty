package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class Post {
    @SerializedName("pinned") boolean isPinned;
    String[] files;
    String[] tags;
    @SerializedName("comments_count") int commentsCount;
    String text;
    String created;
    String type;
    String id;
    @SerializedName("private")boolean isPrivate;

    public boolean isPinned() {
        return isPinned;
    }

    public String[] getFiles() {
        return files;
    }

    public String[] getTags() {
        return tags;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public String getText() {
        return text;
    }

    public String getCreated() {
        return created;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
