package com.ruslan.hlushan.core.ui.api.presentation.lifecycle

import androidx.lifecycle.Lifecycle

/*
 * https://youtu.be/FHScWP8M844?t=11459
 */

@SuppressWarnings("ComplexInterface", "TooManyFunctions")
interface LifecyclePluginObserver {

    fun onBeforeSuperAttach() = Unit
    fun onAfterSuperAttach() = Unit

    fun onBeforeSuperCreate() = Unit
    fun onAfterSuperCreate() = Unit

    fun onBeforeSuperCreateView() = Unit
    fun onAfterSuperViewCreated() = Unit

    fun onBeforeSuperStart() = Unit
    fun onAfterSuperStart() = Unit

    fun onBeforeSuperResume() = Unit
    fun onAfterSuperResume() = Unit

    fun onBeforeSuperPause() = Unit
    fun onAfterSuperPause() = Unit

    fun onBeforeSuperStop() = Unit
    fun onAfterSuperStop() = Unit

    fun onBeforeSuperDestroyView() = Unit
    fun onAfterSuperDestroyView() = Unit

    fun onBeforeSuperDestroy() = Unit
    fun onAfterSuperDestroy() = Unit

    fun onBeforeSuperDetach() = Unit
    fun onAfterSuperDetach() = Unit

    fun onBeforeSuperSaveInstanceState() = Unit
    fun onAfterSuperSaveInstanceState() = Unit

    interface Owner {

        val currentState: Lifecycle.State?

        fun addLifecyclePluginObserver(observer: LifecyclePluginObserver)

        fun removeLifecyclePluginObserver(observer: LifecyclePluginObserver)
    }
}

inline fun Collection<LifecyclePluginObserver>.dispatchEventForAll(onEvent: LifecyclePluginObserver.() -> Unit) {
    val lifecyclePluginObserversCopy = this.toList()
    for (observer: LifecyclePluginObserver in lifecyclePluginObserversCopy) {
        observer.onEvent()
    }
}