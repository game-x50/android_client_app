package com.ruslan.hlushan.core.error

import io.reactivex.Completable
import io.reactivex.Single
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> Single<T>.mapError(block: (Throwable) -> Throwable): Single<T> =
        onErrorResumeNext { throwable -> Single.error(block(throwable)) }

fun Completable.mapError(block: (Throwable) -> Throwable): Completable =
        onErrorResumeNext { throwable -> Completable.error(block(throwable)) }

fun Throwable.isNoNetworkException(): Boolean =
        (this is UnknownHostException
         || this is SocketTimeoutException
         || this is ConnectException)

fun networkErrorMap(error: Throwable): Throwable =
        if (error.isNoNetworkException()) {
            NetworkException(error)
        } else {
            error
        }