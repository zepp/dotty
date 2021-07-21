/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R

class TagsAdapter(@LayoutRes val itemLayout: Int = R.layout.list_item_tag) : RecyclerView.Adapter<TagHolder>() {
    var list: List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onTagClicked: (tag: String) -> Unit = {}
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
                    .let { TagHolder(it) }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TagHolder, position: Int) =
            holder.bind(list[position], onTagClicked)
}

class TagHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(tag: String, onTagClicked: (tag: String) -> Unit) {
        with(itemView as TextView) {
            text = tag
            setOnClickListener { onTagClicked(tag) }
        }
    }
}