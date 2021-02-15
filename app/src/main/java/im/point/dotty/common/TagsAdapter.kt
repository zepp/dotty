package im.point.dotty.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R

class TagsAdapter(var list: List<String> = listOf()) : RecyclerView.Adapter<TagHolder>() {

    fun replaceList(list: List<String>) {
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

class TagHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(tag: String) = (itemView as TextView).setText(tag)
}