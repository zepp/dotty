package im.point.dotty.network

class PostReply (@JvmField var post: RawPost? = null,
                 @JvmField var comments: List<RawComment>? = null): Envelope()