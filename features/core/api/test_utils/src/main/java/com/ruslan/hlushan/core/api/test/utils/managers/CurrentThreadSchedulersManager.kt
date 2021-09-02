package com.ruslan.hlushan.core.api.test.utils.managers

import com.ruslan.hlushan.core.api.managers.SchedulersManager
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class CurrentThreadSchedulersManager : SchedulersManager {

    override val io: Scheduler get() = Schedulers.trampoline()
    override val ui: Scheduler get() = Schedulers.trampoline()
    override val computation: Scheduler get() = Schedulers.trampoline()
}