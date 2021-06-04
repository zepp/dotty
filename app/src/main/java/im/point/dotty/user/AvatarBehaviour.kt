package im.point.dotty.user

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.Space
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import im.point.dotty.R

class AvatarBehaviour(private val context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<ImageView>(context, attrs) {
    private val tag = this::class.simpleName
    private lateinit var toolbar: Toolbar
    private lateinit var placeholder: Space
    private var placeholderBottomY: Float = 0F

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            toolbar = dependency.findViewById(R.id.user_toolbar)
            placeholder = dependency.findViewById(R.id.user_avatar_placeholder)
            placeholderBottomY = placeholder.y + placeholder.height
            return true
        }
        return false
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        val layout = dependency as AppBarLayout
        val params = child.layoutParams
        val size = with(placeholderBottomY + layout.y) {
            // poor man clamp
            if (this > placeholder.width) {
                placeholder.width
            } else {
                this.toInt()
            }
        }
        if (size > 0) {
            params.height = size
            params.width = size
        }
        child.y = toolbar.height + placeholder.y
        child.x = (toolbar.width.toFloat() - size) / 2
        child.layoutParams = params
        return true;
    }
}