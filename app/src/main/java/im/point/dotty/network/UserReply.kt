package im.point.dotty.network

class UserReply : RawUser() {
    val user: RawUser
        get() = this
}