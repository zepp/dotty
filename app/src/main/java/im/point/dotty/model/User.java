package im.point.dotty.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    public long id;
    public String login;
    public String name;
    @ColumnInfo(name = "registration_date")
    public Date registrationDate;
    @ColumnInfo(name = "birth_date")
    public Date birthDate;
    public int gender;
    public String about;
}
