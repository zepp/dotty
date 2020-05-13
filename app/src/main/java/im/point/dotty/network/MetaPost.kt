package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class MetaPost(@JvmField @SerializedName("bookmarked")
                    var isBookmarked : Boolean? = false,
                    @JvmField
                    var uid: Long? = null,

                    @JvmField @SerializedName("subscribed")
                    var isSubscribed : Boolean? = null,

                    @JvmField @SerializedName("editable")
                    var isEditable : Boolean? = null,

                    @JvmField @SerializedName("recommended")
                    var isRecommended : Boolean? = null,
                    @JvmField
                    var post: RawPost? = null)