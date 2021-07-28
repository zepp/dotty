package im.point.dotty.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.use
import im.point.dotty.R

class ToggleImageButton @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0) :
        AppCompatImageButton(context, attrs, defStyleAttr), Checkable {

    private var checked = false
    var onCheckedChangeListener: (value: Boolean) -> Unit = {}

    override fun setChecked(value: Boolean) {
        if (checked != value) {
            checked = value
            onCheckedChangeListener(value)
            refreshDrawableState()
        }
    }

    override fun isChecked() = checked

    override fun toggle() {
        isChecked = !checked
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    init {
        isClickable = true
        isFocusable = true
        context.theme.obtainStyledAttributes(attrs, R.styleable.ToggleImageButton, 0, 0).use {
            isChecked = it.getBoolean(R.styleable.ToggleImageButton_android_checked, false)
        }
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}