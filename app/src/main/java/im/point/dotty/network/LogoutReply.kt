package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class LogoutReply(
        @JvmField @SerializedName("ok")
        var ok: Boolean? = null) : Envelope()