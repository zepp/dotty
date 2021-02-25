/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

import com.google.gson.annotations.SerializedName

data class RawPost(@SerializedName("pinned")
                   var isPinned: Boolean? = null,
                   var files: List<String>? = null,
                   var tags: List<String>? = null,
                   @SerializedName("comments_count")
                   var commentsCount: Int? = null,
                   var text: String? = null,
                   var created: String? = null,
                   var type: String? = null,
                   var id: String? = null,
                   @SerializedName("private")
                   var isPrivate: Boolean? = null,
                   var author: Author? = null)