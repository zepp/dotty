/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class PostsReply(@SerializedName("has_next")
                      var hasNext: Boolean = false,
                      var posts: List<MetaPost> = listOf()) : Envelope()