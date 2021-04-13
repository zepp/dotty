package im.point.dotty.common

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class AvatarOutline(private val curveRadius: Int = 16) : ViewOutlineProvider() {
    override fun getOutline(view: View?, outline: Outline?) {
        view?.let {
            outline?.setRoundRect(0, 0, it.getWidth(), it.getHeight(), curveRadius.toFloat())
        }
    }
}