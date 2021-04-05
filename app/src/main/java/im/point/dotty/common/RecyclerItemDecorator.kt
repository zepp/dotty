package im.point.dotty.common

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.recyclerview.widget.DividerItemDecoration

class RecyclerItemDecorator(context: Context, orientation: Int, dp: Int) : DividerItemDecoration(context, orientation) {
    private val space = (dp * context.resources.displayMetrics.density).toInt()

    init {
        with(ShapeDrawable(RectShape())) {
            if (orientation == VERTICAL) {
                intrinsicHeight = space
            } else if (orientation == HORIZONTAL) {
                intrinsicWidth = space
            }
            alpha = 0
            setDrawable(this)
        }
    }
}