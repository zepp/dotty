package im.point.dotty.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import java.util.Date;

@Entity(tableName = "comments",
        primaryKeys = {"post_id", "id"},
        foreignKeys = {@ForeignKey(entity = Post.class, parentColumns = "id", childColumns = "text_id"),
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id")})
public class Comment {
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
