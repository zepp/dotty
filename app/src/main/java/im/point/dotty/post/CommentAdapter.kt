/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

typealias OnCommentAction = (item: Comment) -> Unit

class CommentAdapter(val scope: CoroutineScope) : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {
    private var list: List<Comment> = listOf()

    private var selectedPosition = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var userId: Long = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var avatarProvider: (name: String) -> Flow<Bitmap> = { emptyFlow() }
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

    var onCommentReply: OnCommentAction = { _ -> }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCommentEdit: OnCommentAction = { _ -> }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCommentRecommend: OnCommentAction = { _ -> }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCommentRemove: OnCommentAction = { _ -> }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun calculateDiffAndDispatchUpdate(new: List<Comment>) {
        DiffUtil.calculateDiff(DiffCallback(list, new)).also { it.dispatchUpdatesTo(this) }
        list = new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
                    .let { CommentHolder(it, scope) }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) = with(list[position]) {
        holder.bind(this, position, avatarProvider(login), position == selectedPosition)
    }

    override fun getItemId(position: Int): Long = list[position].number.toLong()

    init {
        setHasStableIds(true)
    }

    inner class CommentHolder(view: View, val scope: CoroutineScope) : RecyclerView.ViewHolder(view) {
        private val avatar: ImageView = view.findViewById(R.id.comment_author_avatar)
        private val author: TextView = view.findViewById(R.id.comment_author)
        private val text: TextView = view.findViewById(R.id.comment_text)
        private val id: TextView = view.findViewById(R.id.comment_id)
        private val replyTo: TextView = view.findViewById(R.id.comment_number)
        private val arrow: TextView = view.findViewById(R.id.comment_arrow)
        private val actions: LinearLayout = view.findViewById(R.id.comment_actions)
        private val edit: AppCompatImageButton = view.findViewById(R.id.comment_edit)
        private val recommend: AppCompatImageButton = view.findViewById(R.id.comment_recommend)
        private val remove: AppCompatImageButton = view.findViewById(R.id.comment_remove)
        private val reply: AppCompatImageButton = view.findViewById(R.id.comment_reply)
        private var job = scope.launch { }

        fun bind(comment: Comment, position: Int, avatarBitmap: Flow<Bitmap>, isSelected: Boolean) {
            job.cancel()
            job = scope.launch { avatarBitmap.collect { avatar.setImageBitmap(it) } }
            avatar.setOnClickListener { _ -> onUserClicked(comment.userId, comment.login) }
            author.text = comment.formattedLogin
            author.setOnClickListener { _ -> onUserClicked(comment.userId, comment.login) }
            text.text = comment.text
            id.text = comment.number.toString()
            arrow.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
            replyTo.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
            id.setOnClickListener { onIdClicked(comment.number, position) }
            comment.replyTo?.let {
                replyTo.text = it.toString()
                replyTo.setOnClickListener { _ -> onIdClicked(it, position) }
            }
            actions.visibility = if (isSelected) View.VISIBLE else View.GONE
            itemView.setOnClickListener { onCommentReply(comment) }
            itemView.setOnLongClickListener {
                selectedPosition = if (isSelected) -1 else position
                actions.visibility = if (isSelected) View.GONE else View.VISIBLE
                true
            }
            edit.setOnClickListener { onCommentEdit(comment) }
            edit.visibility = if (comment.userId == userId) View.VISIBLE else View.GONE
            recommend.setOnClickListener { onCommentRecommend(comment) }
            remove.setOnClickListener { onCommentRemove(comment) }
            remove.visibility = if (comment.userId == userId) View.VISIBLE else View.GONE
            reply.setOnClickListener { onCommentReply(comment) }
        }

        init {
            avatar.clipToOutline = true
        }
    }
}

internal class DiffCallback(private val old: List<Comment>, private val new: List<Comment>) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition].id == new[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition] == new[newItemPosition]
}