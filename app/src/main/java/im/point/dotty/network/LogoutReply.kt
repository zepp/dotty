package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class LogoutReply(@SerializedName("ok")
                       var ok: Boolean? = null) : Envelope()