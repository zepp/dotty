package im.point.dotty.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.model.Comment

class CommentAdapter : RecyclerView.Adapter<CommentHolder>() {
    var list: List<Comment> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
        return CommentHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) = holder.bind(list[position])

    override fun getItemId(position: Int): Long = list[position].fullId.hashCode().toLong()

    init {
        setHasStableIds(true)
    }
}

class CommentHolder(view: View) : RecyclerView.ViewHolder(view) {
    val author: TextView = view.findViewById(R.id.comment_author)
    val text: TextView = view.findViewById(R.id.comment_text)
    val id: TextView = view.findViewById(R.id.comment_id)
    val replyTo: TextView = view.findViewById(R.id.comment_reply_to)
    val arrow: TextView = view.findViewById(R.id.comment_arrow)

    fun bind(comment: Comment) {
        author.text = comment.nameOrLogin
        text.text = comment.text
        id.text = comment.id.toString()
        if (comment.replyTo == null) {
            arrow.visibility = View.GONE
            replyTo.visibility = View.GONE
        } else {
            replyTo.text = comment.replyTo.toString()
        }
    }
}