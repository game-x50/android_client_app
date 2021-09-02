package com.ruslan.hlushan.rxjava2.test.utils

import io.reactivex.observers.TestObserver

fun <T> TestObserver<T>.assertNotCompleteNoErrorsNoValues(): TestObserver<T> =
        assertNotComplete()
                .assertNoErrors()
                .assertNoValues()