package im.point.dotty.network

data class PostReply (var post: RawPost? = null,
                      var comments: List<RawComment>? = null): Envelope()