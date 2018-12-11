package com.ruslan.hlushan.rxjava2.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.safetyDispose() {
    if (!isDisposed) {
        dispose()
    }
}

//TODO: #write_unit_tests
val Disposable?.isActive: Boolean
    get() = if (this != null) {
        !this.isDisposed
    } else {
        false
    }

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable) = compositeDisposable.add(this)