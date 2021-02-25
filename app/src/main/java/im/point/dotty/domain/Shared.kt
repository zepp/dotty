/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.domain

import im.point.dotty.network.PointAPI
import im.point.dotty.network.SingleCallbackAdapter
import im.point.dotty.network.UnreadCounters
import io.reactivex.Single

class Shared(private val state: AppState, private val api: PointAPI) {
    fun fetchUnreadCounters(): Single<UnreadCounters> {
        return Single.create<UnreadCounters> { emitter ->
            api.getUnreadCounters(state.token ?: throw Exception("invalid token"))
                    .enqueue(SingleCallbackAdapter(emitter))
        }.doOnSuccess { reply ->
            state.updateUnreadPosts(reply.posts)
            state.updateUnreadComments(reply.comments)
            state.updatePrivateUnreadPosts(reply.privatePosts)
            state.updatePrivateUnreadComments(reply.privateComments)
        }
    }
}