/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class MetaPost(@SerializedName("bookmarked")
                    var isBookmarked : Boolean? = null,
                    var uid: Long? = null,
                    @SerializedName("subscribed")
                    var isSubscribed : Boolean? = null,
                    @SerializedName("editable")
                    var isEditable : Boolean? = null,
                    @SerializedName("recommended")
                    var isRecommended : Boolean? = null,
                    var post: RawPost? = null)