/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class UserReply(var id: Long?= null,
                     var login: String?= null,
                     @SerializedName("deny_anonymous")
                     var isDenyAnonymous: Boolean?= null,
                     @SerializedName("private")
                     var isPrivate: Boolean?= null,
                     @SerializedName("subscribed")
                     var isSubscribed: Boolean?= null,
                     @SerializedName("bl")
                     var isBlackListed: Boolean?= null,
                     @SerializedName("wl")
                     var isWhiteListed: Boolean?= null,
                     @SerializedName("rec_sub")
                     var isSubscribedToRecommendations: Boolean?= null) : RawUser() {
    val user: RawUser
        get() = this
}