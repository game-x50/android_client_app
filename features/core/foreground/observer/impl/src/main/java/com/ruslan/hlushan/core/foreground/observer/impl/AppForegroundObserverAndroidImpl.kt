package com.ruslan.hlushan.core.foreground.observer.impl

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.ruslan.hlushan.core.foreground.observer.api.AppForegroundObserver
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import java.util.concurrent.atomic.AtomicBoolean

internal class AppForegroundObserverAndroidImpl : AppForegroundObserver, LifecycleObserver {

    private val atomicIsInForeground = AtomicBoolean(false)

    override val isInForeground: Boolean
        get() = atomicIsInForeground.get()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        atomicIsInForeground.set(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        atomicIsInForeground.set(false)
    }

    companion object {

        fun activateAndProvide(schedulersManager: SchedulersManager): AppForegroundObserver {
            val observer = AppForegroundObserverAndroidImpl()

            schedulersManager.ui.scheduleDirect {
                ProcessLifecycleOwner.get()
                        .lifecycle
                        .addObserver(observer)
            }

            return observer
        }
    }
}