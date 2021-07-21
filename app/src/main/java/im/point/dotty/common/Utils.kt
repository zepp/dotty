package im.point.dotty.common

import android.text.Selection.setSelection
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toCollection
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.security.MessageDigest

fun TextView.setTextAndCursor(text: String) {
    setText(text)
    setSelection(this.editableText, text.length)
}

fun RecyclerView.addOnLastItemDisplayedListener(block: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        val manager = layoutManager as LinearLayoutManager
        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(view, dx, dy)
            adapter?.let {
                if (view.scrollState == RecyclerView.SCROLL_STATE_SETTLING && dy > 0
                    && manager.findLastCompletelyVisibleItemPosition() == it.itemCount - 1
                ) {
                    block()
                }
            }
        }
    })
}

fun String.digest(digest: MessageDigest) = digest.digest(toByteArray())
        .joinToString(separator = "") { String.format("%02X", it) }

fun <T> Flow<T>.toListFlow(): Flow<List<T>> = flow {
    emit(toCollection(mutableListOf()))
}

@ExperimentalCoroutinesApi
fun Call.asFlow() = callbackFlow {
    with(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            close(e)
        }

        override fun onResponse(call: Call, response: Response) {
            try {
                sendBlocking(response)
            } finally {
                close()
            }
        }
    }) {
        enqueue(this)
    }
    awaitClose {
        // flow collector is cancelled but call has not been completed yet
        if (!isClosedForSend) {
            this@asFlow.cancel()
        }
    }
}