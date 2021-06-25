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
import im.point.dotty.common.AvatarOutline
import im.point.dotty.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentAdapter(val scope: CoroutineScope, val factory: (name: String) -> Flow<Bitmap>) : RecyclerView.Adapter<CommentHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
        return CommentHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) = scope.launch(Dispatchers.Main) {
        val item = list[position]
        factory(item.login ?: throw Exception("empty login"))
                .collect { bitmap -> holder.bind(item, position, bitmap, onIdClicked) }
    }.let { Unit }

    override fun getItemId(position: Int): Long = list[position].number.toLong()

    init {
        setHasStableIds(true)
    }
}

class CommentHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val avatar: ImageView = view.findViewById(R.id.comment_author_avatar)
    private val author: TextView = view.findViewById(R.id.comment_author)
    private val text: TextView = view.findViewById(R.id.comment_text)
    private val id: TextView = view.findViewById(R.id.comment_id)
    private val replyTo: TextView = view.findViewById(R.id.comment_reply_to)
    private val arrow: TextView = view.findViewById(R.id.comment_arrow)

    fun bind(comment: Comment, pos: Int, avatarBitmap: Bitmap, onIdClicked: (id: Int, pos: Int) -> Unit) {
        avatar.setImageBitmap(avatarBitmap)
        author.text = comment.nameOrLogin
        text.text = comment.text
        id.text = comment.number.toString()
        arrow.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
        replyTo.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
        id.setOnClickListener { v -> onIdClicked(comment.number, pos) }
        comment.replyTo?.let {
            replyTo.text = it.toString()
            replyTo.setOnClickListener { v -> onIdClicked(it, pos) }
        }
    }

    init {
        avatar.outlineProvider = outline
        avatar.clipToOutline = true
    }

    companion object {
        val outline = AvatarOutline()
    }
}