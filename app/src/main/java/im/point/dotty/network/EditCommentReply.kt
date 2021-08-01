package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class EditCommentReply(
    @SerializedName("id")
    val postId: String,
    @SerializedName("comment_id")
    val number: Int
) : Envelope()