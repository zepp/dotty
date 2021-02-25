/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class CountersReply(var posts: Int? = null,
                         var comments: Int? = null,
                         @SerializedName("private_posts")
                         var privatePosts: Int? = null,
                         @SerializedName("private_comments")
                         var privateComments: Int? = null) : Envelope()