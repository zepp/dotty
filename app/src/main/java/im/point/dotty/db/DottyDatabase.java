package im.point.dotty.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

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

    public abstract AllPostDao getAllPostDao();

    public abstract UserPostDao getUserPostsDao();
}
