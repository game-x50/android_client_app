@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.rxjava2.extensions

import io.reactivex.CompletableEmitter
import io.reactivex.ObservableEmitter
import io.reactivex.SingleEmitter

@SuppressWarnings("TooGenericExceptionCaught")
fun <T : Any> ObservableEmitter<T>.tryEmitOrProvideError(value: T) {
    try {
        if (!this.isDisposed) {
            this.onNext(value)
        }
    } catch (e: Throwable) {
        this.tryOnError(e)
    }
}

@SuppressWarnings("TooGenericExceptionCaught")
fun <T : Any> SingleEmitter<T>.tryEmitOrProvideError(value: T) {
    try {
        if (!this.isDisposed) {
            this.onSuccess(value)
        }
    } catch (e: Throwable) {
        this.tryOnError(e)
    }
}

@SuppressWarnings("TooGenericExceptionCaught")
fun CompletableEmitter.tryEmitOrProvideError() {
    try {
        if (!this.isDisposed) {
            this.onComplete()
        }
    } catch (e: Throwable) {
        this.tryOnError(e)
    }
}