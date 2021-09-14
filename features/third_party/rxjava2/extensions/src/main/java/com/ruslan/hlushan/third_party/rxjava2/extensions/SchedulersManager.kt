@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.rxjava2.extensions

import io.reactivex.Scheduler

interface SchedulersManager {

    val io: Scheduler

    val ui: Scheduler

    val computation: Scheduler
}