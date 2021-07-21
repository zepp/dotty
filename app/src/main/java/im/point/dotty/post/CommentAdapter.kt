/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class CommentAdapter(val scope: CoroutineScope) : RecyclerView.Adapter<CommentHolder>() {

    var avatarProvider: (name: String) -> Flow<Bitmap> = { emptyFlow() }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var list: List<Comment> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onIdClicked: (id: Int, pos: Int) -> Unit = { _, _ -> }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onUserClicked: (id: Long, login: String) -> Unit = { _, _ -> }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
                    .let { CommentHolder(it, scope) }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) = with(list[position]) {
        holder.bind(this, position, avatarProvider(login), onIdClicked, onUserClicked)
    }

    override fun getItemId(position: Int): Long = list[position].number.toLong()

    init {
        setHasStableIds(true)
    }
}

class CommentHolder(view: View, val scope: CoroutineScope) : RecyclerView.ViewHolder(view) {
    private val avatar: ImageView = view.findViewById(R.id.comment_author_avatar)
    private val author: TextView = view.findViewById(R.id.comment_author)
    private val text: TextView = view.findViewById(R.id.comment_text)
    private val id: TextView = view.findViewById(R.id.comment_id)
    private val replyTo: TextView = view.findViewById(R.id.comment_reply_to)
    private val arrow: TextView = view.findViewById(R.id.comment_arrow)
    private var job = scope.launch { }

    fun bind(comment: Comment, pos: Int, avatarBitmap: Flow<Bitmap>,
             onIdClicked: (id: Int, pos: Int) -> Unit, onUserClicked: (id: Long, login: String) -> Unit) {
        job.cancel()
        job = scope.launch { avatarBitmap.collect { avatar.setImageBitmap(it) } }
        avatar.setOnClickListener { _ -> onUserClicked(comment.userId, comment.login) }
        author.text = comment.formattedLogin
        author.setOnClickListener { _ -> onUserClicked(comment.userId, comment.login) }
        text.text = comment.text
        id.text = comment.number.toString()
        arrow.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
        replyTo.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
        id.setOnClickListener { onIdClicked(comment.number, pos) }
        comment.replyTo?.let {
            replyTo.text = it.toString()
            replyTo.setOnClickListener { _ -> onIdClicked(it, pos) }
        }
    }

    init {
        avatar.clipToOutline = true
    }
}