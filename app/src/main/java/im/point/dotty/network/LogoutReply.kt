/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class LogoutReply(@SerializedName("ok")
                       var ok: Boolean? = null) : Envelope()