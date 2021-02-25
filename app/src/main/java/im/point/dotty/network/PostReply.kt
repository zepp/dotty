/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

data class PostReply (var post: RawPost? = null,
                      var comments: List<RawComment>? = null): Envelope()