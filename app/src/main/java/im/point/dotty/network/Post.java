package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Post {
    @SerializedName("pinned") boolean isPinned;
    List<String> files;
    List<String> tags;
    @SerializedName("comments_count") int commentsCount;
    String text;
    String created;
    String type;
    String id;
    @SerializedName("private")boolean isPrivate;

    public boolean isPinned() {
        return isPinned;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<String> getTags() {
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
