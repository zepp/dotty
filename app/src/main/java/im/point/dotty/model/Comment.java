package im.point.dotty.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

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
