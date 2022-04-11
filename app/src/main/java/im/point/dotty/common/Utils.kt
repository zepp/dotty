package im.point.dotty.common

import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.CheckResult
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.security.MessageDigest

fun AppCompatEditText.replaceText(value: String) {
    text?.apply {
        if (toString() != value) {
            replace(0, length, value)
        }
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
@CheckResult
fun AppCompatEditText.onTextChanged(millis: Long) = callbackFlow {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            trySend(s.toString())
        }
    }
    addTextChangedListener(listener)
    trySend(text.toString())
    awaitClose { removeTextChangedListener(listener) }
}.debounce(millis).buffer(1, BufferOverflow.DROP_OLDEST)

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

private fun LifecycleOwner.repeatOnLifecycle(
        block: suspend CoroutineScope.() -> Unit,
        state: Lifecycle.State
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state, block)
    }
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    repeatOnLifecycle(block, Lifecycle.State.STARTED)
}