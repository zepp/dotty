package im.point.dotty.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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
