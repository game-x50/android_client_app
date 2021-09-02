package com.ruslan.hlushan.core.api.managers

import io.reactivex.Scheduler

interface SchedulersManager {

    val io: Scheduler

    val ui: Scheduler

    val computation: Scheduler
}