package com.ruslan.hlushan.core.ui.api.extensions

import androidx.lifecycle.Lifecycle
import com.ruslan.hlushan.core.api.utils.thread.SingleThreadSafety
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.checkThread
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/*
 * https://github.com/kirich1409/ViewBindingPropertyDelegate
 */

@SingleThreadSafety
internal abstract class AbstractViewLifecycleBinder<in Owner : LifecyclePluginObserver.Owner, out V : Any> : ReadOnlyProperty<Owner, V?> {

    private val lifecyclePluginObserver = object : LifecyclePluginObserver {

        override fun onBeforeSuperDestroyView() = clearAll()
    }

    private var lifecyclePluginOwner: LifecyclePluginObserver.Owner? = null
    private var cached: V? = null

    protected abstract val threadChecker: ThreadChecker

    protected abstract fun tryCreateValue(thisRef: Owner): V?

    override fun getValue(thisRef: Owner, property: KProperty<*>): V? {
        threadChecker.checkThread()

        val currentState = thisRef.currentState

        if ((cached == null)
            && (currentState != null) && (currentState >= Lifecycle.State.INITIALIZED)) {

            val createdValue: V? = tryCreateValue(thisRef)

            if (createdValue != null) {

                cached = createdValue

                lifecyclePluginOwner = thisRef

                thisRef.addLifecyclePluginObserver(lifecyclePluginObserver)
            }
        }

        return cached
    }

    private fun clearAll() {
        threadChecker.checkThread()

        if (cached != null) {

            lifecyclePluginOwner?.removeLifecyclePluginObserver(lifecyclePluginObserver)

            lifecyclePluginOwner = null
            cached = null
        }
    }
}