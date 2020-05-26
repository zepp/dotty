package im.point.dotty.repository

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import im.point.dotty.db.DottyDatabase
import im.point.dotty.domain.AppState
import im.point.dotty.mapper.AllPostMapper
import im.point.dotty.mapper.CommentedPostMapper
import im.point.dotty.mapper.RecentPostMapper
import im.point.dotty.model.AllPost
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.network.PointAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepoFactory(context: Context) {
    private val BASE = "https://point.im"
    private val gson: Gson
    private val retrofit: Retrofit
    private val api: PointAPI
    private val database: DottyDatabase
    private val state: AppState

    fun getRecentRepo(): Repository<RecentPost> {
        return RecentRepo(api, state.token, database.getRecentPostDao(), RecentPostMapper())
    }

    fun getCommentedRepo(): Repository<CommentedPost> {
        return CommentedRepo(api, state.token, database.getCommentedPostDao(), CommentedPostMapper())
    }

    fun getAllRepo(): Repository<AllPost> {
        return AllRepo(api, state.token, database.getAllPostDao(), AllPostMapper())
    }

    init {
        gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        api = retrofit.create(PointAPI::class.java)
        database = Room.databaseBuilder(context.applicationContext, DottyDatabase::class.java, "main").build()
        state = AppState.getInstance(context)
    }
}