/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class LoginReply(var token: String? = null,
                      @SerializedName("csrf_token")
                      var csrfToken: String? = null) : Envelope()