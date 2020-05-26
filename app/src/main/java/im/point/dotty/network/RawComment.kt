package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class RawComment(var created: String? = null,
                      var text: String? = null,
                      var author: Author? = null,
                      var id : Int? = null,
                      @SerializedName("post_id")
                      var postId: String? = null,
                      @SerializedName("to_comment_id")
                      var toCommentId : Int? = null,
                      @SerializedName("is_rec")
                      var isRecommendation : Boolean? = null)