package im.point.dotty.user

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import im.point.dotty.R

class AvatarBehaviour(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<ImageView>(context, attrs) {
    private val tag = this::class.simpleName
    private val scale = context.resources.displayMetrics.density
    private lateinit var toolbar: Toolbar
    private lateinit var actions: LinearLayout
    private var maxSize: Int = 0
    private var baseX: Float = 0F
    private var baseY: Float = 0F
    private val margin = pixFrom(16)

    private fun pixFrom(dp: Int) = (dp * scale + 0.5f).toInt()

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            toolbar = dependency.findViewById(R.id.toolbar)
            maxSize = toolbar.height
            baseX = toolbar.width - (maxSize + margin * 2) / 2F
            baseY = toolbar.height.toFloat()
            actions = dependency.findViewById(R.id.user_actions)
            return true
        }
        return false
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        val layout = dependency as AppBarLayout
        val params = child.layoutParams

        val size = (layout.height + layout.y - toolbar.height - actions.height)
                .let { if (it <= maxSize) it else maxSize.toFloat() }
        if (size > 0) {
            params.height = size.toInt()
            params.width = size.toInt()
            child.y = baseY - size / 2
            child.x = baseX - size / 2
            child.visibility = View.VISIBLE
        } else {
            child.visibility = View.GONE
        }
        child.layoutParams = params
        return true;
    }
}