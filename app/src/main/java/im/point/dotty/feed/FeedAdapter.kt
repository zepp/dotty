/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.model.Post

class FeedAdapter<T : Post> internal constructor() : RecyclerView.Adapter<PostHolder<T>>() {
    var list: List<T> = listOf()
        get() = field
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClicked: (item: T) -> Unit = {}

    var onUserClicked: (item: Long) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_post, parent, false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder<T>, position: Int) =
            holder.bind(list[position], onItemClicked, onUserClicked)

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = list[position].id.hashCode().toLong()

    init {
        setHasStableIds(true)
    }
}