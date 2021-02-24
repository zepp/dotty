package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class UnreadCounters(
        var posts: Int,
        var comments: Int,
        @SerializedName("private_posts")
        var privatePosts: Int,
        @SerializedName("private_comments")
        var privateComments: Int) : Envelope()