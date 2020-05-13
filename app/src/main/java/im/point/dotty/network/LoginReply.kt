package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class LoginReply(@JvmField var token: String? = null,
                      @JvmField @SerializedName("csrf_token")
                      var csrfToken: String? = null) : Envelope()