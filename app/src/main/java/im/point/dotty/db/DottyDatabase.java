package im.point.dotty.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import im.point.dotty.model.AllPost;
import im.point.dotty.model.Comment;
import im.point.dotty.model.CommentedPost;
import im.point.dotty.model.RecentPost;
import im.point.dotty.model.User;
import im.point.dotty.model.UserPost;

@Database(entities = {User.class, RecentPost.class, CommentedPost.class, AllPost.class, UserPost.class, Comment.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class DottyDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();

    public abstract RecentPostDao getRecentPostDao();

    public abstract CommentedPostDao getCommentedPostDao();

    public abstract AllPostDao getAllDao();

    public abstract UserPostDao getUserPostsDao();

    public abstract CommentDao getCommentDao();
}
