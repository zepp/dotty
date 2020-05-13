package im.point.dotty.network

data class Author(@JvmField var login: String? = null,
                  @JvmField var id: Long? = null,
                  @JvmField var avatar: String? = null,
                  @JvmField var name: String? = null)