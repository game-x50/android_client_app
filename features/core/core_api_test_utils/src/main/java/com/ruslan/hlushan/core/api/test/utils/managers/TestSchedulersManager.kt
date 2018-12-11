package com.ruslan.hlushan.core.api.test.utils.managers

import com.ruslan.hlushan.core.api.managers.SchedulersManager
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class TestSchedulersManager : SchedulersManager {

    private val testScheduler = TestScheduler()

    override val io: Scheduler get() = testScheduler
    override val ui: Scheduler get() = testScheduler
    override val computation: Scheduler get() = testScheduler

    fun advanceTimeBy(delayTime: Long, unit: TimeUnit) = testScheduler.advanceTimeBy(delayTime, unit)

    fun advanceTimeTo(delayTime: Long, unit: TimeUnit) = testScheduler.advanceTimeTo(delayTime, unit)

    fun triggerActions() = testScheduler.triggerActions()
}