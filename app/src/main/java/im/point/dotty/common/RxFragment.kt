/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.common

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class RxFragment : Fragment() {
    private val composite = CompositeDisposable()
    protected fun addDisposable(disposable: Disposable?) {
        composite.add(disposable!!)
    }

    override fun onStop() {
        super.onStop()
        composite.clear()
    }
}