package im.point.dotty.feed

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.model.Post

class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val author: TextView
    private val id: TextView
    private val text: TextView
    private val tags: TextView

    fun bind(post: Post) {
        author.text = if (post.name == null || post.name?.isEmpty() == true) post.login else post.name
        id.text = "#" + post.postId
        text.text = post.text
    }

    init {
        author = itemView.findViewById(R.id.post_author)
        id = itemView.findViewById(R.id.post_id)
        text = itemView.findViewById(R.id.post_text)
        tags = itemView.findViewById(R.id.post_tags)
    }
}