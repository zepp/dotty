package im.point.dotty.network

import com.google.gson.annotations.SerializedName

open class RawUser(@JvmField
                   var about: String? = null,
                   @JvmField
                   var xmpp: String? = null,
                   @JvmField
                   var name: String? = null,
                   @JvmField @SerializedName("deny anonymous")
                   var isDenyAnonymous: Boolean? = null,
                   @JvmField @SerializedName("private")
                   var isPrivate: Boolean? = null,
                   @JvmField @SerializedName("subscribed")
                   var isSubscribed: Boolean? = null,
                   @JvmField
                   var created: String? = null,
                   @JvmField @SerializedName("bl")
                   var isBlackListed: Boolean? = null,
                   @JvmField
                   var gender: Boolean? = null,
                   @JvmField @SerializedName("wl")
                   var isWhiteListed: Boolean? = null,
                   @JvmField
                   var birthdate: String? = null,
                   @JvmField
                   var id: Long? = null,
                   @JvmField @SerializedName("rec sub")
                   var isSubscribedToRecommendations: Boolean? = null,
                   @JvmField
                   var avatar: String? = null,
                   @JvmField
                   var skype: String? = null,
                   @JvmField
                   var login: String? = null,
                   @JvmField
                   var icq: String? = null,
                   @JvmField
                   var homepage: String? = null,
                   @JvmField
                   var email: String? = null,
                   @JvmField
                   var location: String? = null) : Envelope()