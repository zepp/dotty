package im.point.dotty.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public abstract class Post {
    @PrimaryKey
    public long id;
    @ColumnInfo(name =  "user_id")
    public long userId;
    public String login;
    public String name;
    @ColumnInfo(name =  "text_id")
    public String textId;
    public String text;
    public Date timestamp;
}
