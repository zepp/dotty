package im.point.dotty.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import im.point.dotty.model.*

@Database(entities = [User::class, RecentPost::class, CommentedPost::class, AllPost::class, UserPost::class, Comment::class], version = 1)
@TypeConverters(value = [DateConverter::class, GenderConverter::class, StringListConverter::class])
abstract class DottyDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getRecentPostDao(): RecentPostDao
    abstract fun getCommentedPostDao(): CommentedPostDao
    abstract fun getAllPostDao(): AllPostDao
    abstract fun getUserPostsDao(): UserPostDao
    abstract fun getCommentDao() : CommentDao
}