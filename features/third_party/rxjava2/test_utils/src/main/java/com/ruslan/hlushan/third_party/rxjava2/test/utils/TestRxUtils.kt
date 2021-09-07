package com.ruslan.hlushan.third_party.rxjava2.test.utils

import io.reactivex.observers.TestObserver

fun <T> TestObserver<T>.assertNotCompleteNoErrorsNoValues(): TestObserver<T> =
        assertNotComplete()
                .assertNoErrors()
                .assertNoValues()