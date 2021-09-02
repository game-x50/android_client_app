package com.ruslan.hlushan.rxjava2.extensions

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable

@CheckReturnValue
fun <T> Observable<T>.safetySubscribe(): Disposable =
        ignoreElements()
                .safetySubscribe()

@CheckReturnValue
fun <T> Flowable<T>.safetySubscribe(): Disposable =
        ignoreElements()
                .safetySubscribe()

@CheckReturnValue
fun <T> Single<T>.safetySubscribe(): Disposable =
        ignoreElement()
                .safetySubscribe()

@CheckReturnValue
fun <T> Maybe<T>.safetySubscribe(): Disposable =
        ignoreElement()
                .safetySubscribe()

@CheckReturnValue
fun Completable.safetySubscribe(): Disposable =
        onErrorComplete()
                .subscribe()