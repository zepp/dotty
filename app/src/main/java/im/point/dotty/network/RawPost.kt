package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class RawPost(@JvmField @SerializedName("pinned")
                   var isPinned: Boolean? = null,
                   @JvmField
                   var files: List<String>? = null,
                   @JvmField
                   var tags: List<String>? = null,
                   @JvmField @SerializedName("comments_count")
                   var commentsCount: Int? = null,
                   @JvmField
                   var text: String? = null,
                   @JvmField
                   var created: String? = null,
                   @JvmField
                   var type: String? = null,
                   @JvmField
                   var id: String? = null,
                   @JvmField @SerializedName("private")
                   var isPrivate: Boolean? = null,
                   @JvmField
                   var author: Author? = null)