package im.point.dotty.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.model.Post


class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val author: TextView
    private val id: TextView
    private val text: TextView
    private val tags: RecyclerView
    private val adapter: TagsAdapter

    fun bind(post: Post) {
        author.text = if (post.name == null || post.name?.isEmpty() == true) post.login else post.name
        id.text = "#" + post.postId
        text.text = post.text
        if (post.tags.isNullOrEmpty()) {
            tags.visibility = View.GONE;
        } else{
            adapter.replaceList(post.tags!!)
        }
    }

    init {
        adapter = TagsAdapter()
        author = itemView.findViewById(R.id.post_author)
        id = itemView.findViewById(R.id.post_id)
        text = itemView.findViewById(R.id.post_text)
        tags = itemView.findViewById(R.id.post_tags)
        tags.adapter = adapter
        tags.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
    }
}

internal class TagsAdapter (var list : List<String> = listOf()): RecyclerView.Adapter<TagHolder>() {

    fun replaceList(list : List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_tag, parent, false)
        return TagHolder(view)
    }
    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TagHolder, position: Int) = holder.bind(list.get(position))
}

internal class TagHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(tag: String) = (itemView as TextView).setText(tag)
}
