package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class PostsReply(@SerializedName("has_next")
                 var hasNext : Boolean? = null,
                 var posts: List<MetaPost>? = null) : Envelope()