package im.point.dotty.common

import android.text.Selection.setSelection
import android.widget.TextView

fun TextView.setTextAndCursor(text: String) {
    setText(text)
    setSelection(this.editableText, text.length)
}