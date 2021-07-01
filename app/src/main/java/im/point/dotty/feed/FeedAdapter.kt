/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.feed

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R
import im.point.dotty.model.CompletePost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FeedAdapter<T : CompletePost<*>> internal constructor(val scope: CoroutineScope, val factory: (name: String) -> Flow<Bitmap>) : RecyclerView.Adapter<PostHolder<T>>() {
    var list: List<T> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClicked: (item: T) -> Unit = {}

    var onUserClicked: (id: Long, login: String) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder<T> =
            LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
                    .let { PostHolder(it, scope) }

    override fun onBindViewHolder(holder: PostHolder<T>, position: Int) = with(list[position]) {
            holder.bind(this, factory(authorLogin), onItemClicked, onUserClicked)
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = list[position].id.hashCode().toLong()

    init {
        setHasStableIds(true)
    }
}