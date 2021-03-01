/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.feed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.common.TagsAdapter
import im.point.dotty.model.Post


class PostHolder<T : Post>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val author: TextView
    private val id: TextView
    private val text: TextView
    private val tags: RecyclerView
    private val adapter: TagsAdapter
    private val bookmarked: ImageView
    private val recommended: ImageView
    private val commentsCount: TextView

    fun bind(post: T, onItemClicked: (item: T) -> Unit, onUserClicked: (item: Long) -> Unit) {
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
        adapter = TagsAdapter()
        author = itemView.findViewById(R.id.post_author)
        id = itemView.findViewById(R.id.post_id)
        text = itemView.findViewById(R.id.post_text)
        tags = itemView.findViewById(R.id.post_tags)
        commentsCount = itemView.findViewById(R.id.post_comments_count)
        recommended = itemView.findViewById(R.id.post_recommended)
        bookmarked = itemView.findViewById(R.id.post_bookmarked)
        tags.adapter = adapter
        tags.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
    }
}

