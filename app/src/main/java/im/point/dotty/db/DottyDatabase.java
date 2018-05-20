package im.point.dotty.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import im.point.dotty.model.Comment;
import im.point.dotty.model.Post;
import im.point.dotty.model.User;

@Database(entities = {User.class, Post.class, Comment.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class DottyDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
    public abstract PostDao getPostDao();
    public abstract CommentDao getCommentDao();
}
