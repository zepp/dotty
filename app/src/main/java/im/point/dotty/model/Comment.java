package im.point.dotty.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "comments",
        primaryKeys = {"post_id", "id"})
public class Comment {
    @NonNull
    @ColumnInfo(name = "post_id")
    public String postId;
    public int id;
    @ColumnInfo(name = "parent_id")
    public int parentId;
    @ColumnInfo(name = "user_id")
    public long userId;
    public String text;
    public Date timestamp;
}
