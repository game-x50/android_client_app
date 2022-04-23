@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.rxjava2.test.utils

import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class CurrentThreadSchedulersManager : SchedulersManager {

    override val io: Scheduler get() = Schedulers.trampoline()
    override val ui: Scheduler get() = Schedulers.trampoline()
    override val computation: Scheduler get() = Schedulers.trampoline()
}