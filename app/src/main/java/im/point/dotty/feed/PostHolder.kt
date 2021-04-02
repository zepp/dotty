/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.feed

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.common.TagsAdapter
import im.point.dotty.model.Post


class PostHolder<T : Post>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val avatar: ImageView = itemView.findViewById(R.id.post_author_avatar)
    private val author: TextView = itemView.findViewById(R.id.post_author)
    private val id: TextView = itemView.findViewById(R.id.post_id)
    private val text: TextView = itemView.findViewById(R.id.post_text)
    private val tags: RecyclerView = itemView.findViewById(R.id.post_tags)
    private val commentsCount: TextView = itemView.findViewById(R.id.post_comments_count)
    private val recommended: ImageView = itemView.findViewById(R.id.post_recommended)
    private val bookmarked: ImageView = itemView.findViewById(R.id.post_bookmarked)
    private val adapter: TagsAdapter = TagsAdapter()

    fun bind(post: T, bitmap: Bitmap, onItemClicked: (item: T) -> Unit, onUserClicked: (item: Long) -> Unit) {
        avatar.setImageBitmap(bitmap)
        bookmarked.visibility = if (post.bookmarked == true) View.VISIBLE else View.GONE
        recommended.visibility = if (post.recommended == true) View.VISIBLE else View.GONE
        author.text = post.nameOrLogin
        author.setOnClickListener { onUserClicked(post.authorId) }
        id.text = "#" + post.id
        text.text = post.text
        if (post.tags.isNullOrEmpty()) {
            tags.visibility = View.GONE;
        } else {
            adapter.replaceList(post.tags!!)
        }
        commentsCount.text = post.commentCount.toString()
        itemView.setOnClickListener { onItemClicked(post) }
    }

    init {
        tags.adapter = adapter
        tags.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
    }
}

