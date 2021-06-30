/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

data class PostReply(var post: RawPost,
                     var comments: List<RawComment> = listOf()) : Envelope()