@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.rxjava2.test.utils

import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TestSchedulerCleanUpRule(
        private val testScheduler: () -> TestScheduler
) : TestWatcher() {

    override fun finished(description: Description?) {
        super.finished(description)
        testScheduler().triggerActions()
    }
}