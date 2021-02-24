package im.point.dotty.network

data class UnreadCounters(var unreadPosts: Int,
                          var unreadComments: Int,
                          var privateUnreadPosts: Int,
                          var privateUnreadComments: Int)