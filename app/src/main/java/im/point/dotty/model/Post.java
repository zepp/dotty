package im.point.dotty.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "posts",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
public class Post {
    @PrimaryKey
    public long id;
    @ColumnInfo(name =  "user_id")
    public long userId;
    @ColumnInfo(name =  "text_id")
    public String textId;
    public String text;
    Date timestamp;
}
