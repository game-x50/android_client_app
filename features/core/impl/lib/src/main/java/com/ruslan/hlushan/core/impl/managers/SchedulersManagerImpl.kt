package com.ruslan.hlushan.core.impl.managers

import com.ruslan.hlushan.core.api.managers.SchedulersManager
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class SchedulersManagerImpl @Inject constructor() : SchedulersManager {

    override val io: Scheduler get() = Schedulers.io()

    override val ui: Scheduler get() = AndroidSchedulers.mainThread()

    override val computation: Scheduler get() = Schedulers.computation()
}