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

    fun bind(post: T, onItemClicked: (item: T) -> Unit) {
        author.text = post.nameOrLogin
        id.text = "#" + post.postId
        text.text = post.text
        if (post.tags.isNullOrEmpty()) {
            tags.visibility = View.GONE;
        } else {
            adapter.replaceList(post.tags!!)
        }
        itemView.setOnClickListener { onItemClicked(post) }
        bookmarked.visibility = View.GONE
        recommended.visibility = View.GONE
    }

    init {
        adapter = TagsAdapter()
        author = itemView.findViewById(R.id.post_author)
        id = itemView.findViewById(R.id.post_id)
        text = itemView.findViewById(R.id.post_text)
        tags = itemView.findViewById(R.id.post_tags)
        recommended = itemView.findViewById(R.id.post_recommended)
        bookmarked = itemView.findViewById(R.id.post_bookmarked)
        tags.adapter = adapter
        tags.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
    }
}

