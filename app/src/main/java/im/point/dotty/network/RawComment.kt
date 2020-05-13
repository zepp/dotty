package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class RawComment(@JvmField var created: String? = null,
                      @JvmField var text: String? = null,
                      @JvmField var author: Author? = null,
                      @JvmField var id : Int? = null,
                      @JvmField @SerializedName("post id")
                      var postId: String? = null,
                      @JvmField @SerializedName("to comment id")
                      var toCommentId : Int? = null,
                      @JvmField @SerializedName("is rec")
                      var isRecommendation : Boolean? = null)