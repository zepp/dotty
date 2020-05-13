package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class CountersReply(@JvmField var posts: Int? = null,
                         @JvmField var comments: Int? = null,
                         @JvmField @SerializedName("private posts")
                         var privatePosts: Int? = null,
                         @JvmField @SerializedName("private_comments")
                         var privateComments: Int? = null) : Envelope()