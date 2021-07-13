package im.point.dotty.post

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import im.point.dotty.R

class BitmapAdapter : RecyclerView.Adapter<BitmapHolder>() {
    var list: List<Bitmap> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BitmapHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_image, parent, false)
                    .let { BitmapHolder(it) }

    override fun onBindViewHolder(holder: BitmapHolder, position: Int) = with(list[position]) {
        holder.bind(this)
    }

    override fun getItemCount(): Int = list.size
}

class BitmapHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(bitmap: Bitmap) = (itemView as ImageView).setImageBitmap(bitmap)
}