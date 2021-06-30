/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.feed

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.common.AvatarOutline
import im.point.dotty.common.RecyclerItemDecorator
import im.point.dotty.common.TagsAdapter
import im.point.dotty.model.CompletePost


class PostHolder<T : CompletePost<*>>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val avatar: ImageView = itemView.findViewById(R.id.post_author_avatar)
    private val author: TextView = itemView.findViewById(R.id.post_author)
    private val id: TextView = itemView.findViewById(R.id.post_id)
    private val text: TextView = itemView.findViewById(R.id.post_text)
    private val tags: RecyclerView = itemView.findViewById(R.id.post_tags)
    private val commentsCount: TextView = itemView.findViewById(R.id.post_comments_count)
    private val recommended: ImageView = itemView.findViewById(R.id.post_recommended)
    private val bookmarked: ImageView = itemView.findViewById(R.id.post_bookmarked)
    private val adapter: TagsAdapter = TagsAdapter()

    fun bind(post: T, bitmap: Bitmap, onItemClicked: (item: T) -> Unit, onUserClicked: (id: Long, login: String) -> Unit) {
        avatar.setImageBitmap(bitmap)
        post.metapost.let {
            bookmarked.visibility = if (it.bookmarked) View.VISIBLE else View.GONE
            recommended.visibility = if (it.recommended) View.VISIBLE else View.GONE
        }
        post.post.let {
            avatar.setOnClickListener { _ -> onUserClicked(it.authorId, it.authorLogin) }
            author.text = it.alogin
            author.setOnClickListener { _ -> onUserClicked(it.authorId, it.authorLogin) }
            id.text = "#" + post.id
            text.text = it.text
            if (it.tags.isNullOrEmpty()) {
                tags.visibility = View.GONE;
            } else {
                adapter.replaceList(it.tags!!)
            }
            commentsCount.text = it.commentCount.toString()
        }

        itemView.setOnClickListener { onItemClicked(post) }
    }

    init {
        tags.adapter = adapter
        tags.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        tags.addItemDecoration(RecyclerItemDecorator(itemView.context, DividerItemDecoration.HORIZONTAL, 4))
        avatar.outlineProvider = outline
        avatar.clipToOutline = true
    }

    companion object {
        val outline = AvatarOutline()
    }
}

