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

    var onIdClicked: (id: Int, pos: Int) -> Unit = { _: Int, _: Int -> }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
        return CommentHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) =
            holder.bind(list[position], position, onIdClicked)

    override fun getItemId(position: Int): Long = list[position].id.toLong()

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

    fun bind(comment: Comment, pos: Int, onIdClicked: (id: Int, pos: Int) -> Unit) {
        author.text = comment.nameOrLogin
        text.text = comment.text
        id.text = comment.id.toString()
        id.setOnClickListener { v -> onIdClicked(comment.id, pos) }
        arrow.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
        replyTo.visibility = if (comment.replyTo == null) View.GONE else View.VISIBLE
        comment.replyTo?.let {
            replyTo.text = it.toString()
            replyTo.setOnClickListener { v -> onIdClicked(it, pos) }
        }
    }
}