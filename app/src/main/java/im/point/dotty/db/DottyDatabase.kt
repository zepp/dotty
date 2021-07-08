/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import im.point.dotty.BuildConfig
import im.point.dotty.model.*

@Database(entities = [User::class,
    RecentPost::class, CommentedPost::class, AllPost::class, UserPost::class, TaggedPost::class,
    Post::class, Comment::class, TagLastPageId::class, UserData::class, UserTag::class], version = BuildConfig.VERSION_CODE)
@TypeConverters(value = [DateConverter::class, GenderConverter::class, StringListConverter::class])
abstract class DottyDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getRecentPostDao(): RecentPostDao
    abstract fun getCommentedPostDao(): CommentedPostDao
    abstract fun getAllPostDao(): AllPostDao
    abstract fun getUserPostsDao(): UserPostDao
    abstract fun getCommentDao(): CommentDao
    abstract fun getPostDao(): PostDao
    abstract fun getTaggedPostDao(): TaggedPostDao
    abstract fun getTagLastPageIdDao(): TagLastPageIdDao
    abstract fun getUserDataDao(): UserDataDao
    abstract fun getUserTagDao(): UserTagDao
}