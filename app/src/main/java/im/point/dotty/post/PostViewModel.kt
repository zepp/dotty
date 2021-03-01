/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import androidx.lifecycle.ViewModel
import im.point.dotty.DottyApplication
import im.point.dotty.common.AppState
import im.point.dotty.common.Shared
import im.point.dotty.model.AllPost
import im.point.dotty.model.Comment
import im.point.dotty.model.CommentedPost
import im.point.dotty.model.RecentPost
import im.point.dotty.network.Envelope
import im.point.dotty.network.PointAPI
import im.point.dotty.network.SingleCallbackAdapter
import im.point.dotty.repository.RepoFactory
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers

class PostViewModel(application: DottyApplication) : ViewModel() {
    private val repoFactory: RepoFactory
    private val state: AppState = application.state
    private val api: PointAPI = application.mainApi
    private val shared: Shared = Shared(application.baseContext, application.state, application.mainApi)
    private lateinit var pinEmitter: ObservableEmitter<Boolean>

    lateinit var postId: String

    val isPinVisible: Observable<Boolean> = Observable.create<Boolean> { emitter -> pinEmitter = emitter }
            .distinctUntilChanged()
            .replay(1)
            .autoConnect()
            .observeOn(AndroidSchedulers.mainThread())

    fun getRecentPost(): Flowable<RecentPost> {
        return repoFactory.getRecentPostRepo().getItem(postId)
                .doAfterNext { post -> pinEmitter.onNext(post.userId == state.id) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCommentedPost(): Flowable<CommentedPost> {
        return repoFactory.getCommentedPostRepo().getItem(postId)
                .doAfterNext { post -> pinEmitter.onNext(post.userId == state.id) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllPost(): Flowable<AllPost> {
        return repoFactory.getAllPostRepo().getItem(postId)
                .doAfterNext { post -> pinEmitter.onNext(post.userId == state.id) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getRecentPostComments(): Flowable<List<Comment>> {
        return repoFactory.getRecentCommentRepo(postId).getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun getCommentedPostComments(): Flowable<List<Comment>> {
        return repoFactory.getCommentedCommentRepo(postId).getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllPostComments(): Flowable<List<Comment>> {
        return repoFactory.getAllCommentRepo(postId).getAll().observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchRecentPostComments(): Completable {
        return Completable.fromSingle(repoFactory.getRecentCommentRepo(postId).fetch()
                .flatMap { shared.fetchUnreadCounters() })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchCommentedPostComments(): Completable {
        return Completable.fromSingle(repoFactory.getCommentedCommentRepo(postId).fetch()
                .flatMap { shared.fetchUnreadCounters() })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchAllPostComments(): Completable {
        return Completable.fromSingle(repoFactory.getAllCommentRepo(postId).fetch()
                .flatMap { shared.fetchUnreadCounters() })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun subscribe() = Completable.fromSingle(Single.create<Envelope> { emitter ->
        api.subscribeToPost(state.token ?: throw Exception("invalidToken"), postId)
                .enqueue(SingleCallbackAdapter(emitter))
    })

    fun unsubscribe() = Completable.fromSingle(Single.create<Envelope> { emitter ->
        api.unsubscribeFromPost(state.token ?: throw Exception("invalidToken"), postId)
                .enqueue(SingleCallbackAdapter(emitter))
    })

    fun recommend() = Completable.fromSingle(Single.create<Envelope> { emitter ->
        api.recommendPost(state.token ?: throw Exception("invalidToken"), postId)
                .enqueue(SingleCallbackAdapter(emitter))
    })

    fun unrecommend() = Completable.fromSingle(Single.create<Envelope> { emitter ->
        api.unrecommendPost(state.token ?: throw Exception("invalidToken"), postId)
                .enqueue(SingleCallbackAdapter(emitter))
    })

    fun bookmark() = Completable.fromSingle(Single.create<Envelope> { emitter ->
        api.bookmarkPost(state.token ?: throw Exception("invalidToken"), postId)
                .enqueue(SingleCallbackAdapter(emitter))
    })

    fun unbookmark() = Completable.fromSingle(Single.create<Envelope> { emitter ->
        api.unbookmarkPost(state.token ?: throw Exception("invalidToken"), postId)
                .enqueue(SingleCallbackAdapter(emitter))
    })

    init {
        repoFactory = application.repoFactory
        isPinVisible.subscribe()
    }
}