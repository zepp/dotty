package im.point.dotty.network

import com.google.gson.annotations.SerializedName

class PostsReply(@JvmField @SerializedName("has next")
                 var hasNext : Boolean? = null,
                 @JvmField
                 var posts: List<MetaPost>? = null) : Envelope()