package com.ruslan.hlushan.core.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LockableHandlerLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.dispatchEventForAll
import com.ruslan.hlushan.core.ui.api.utils.LockableHandler

abstract class BaseDialogFragment : DialogFragment(), LifecyclePluginObserver.Owner {

    @get:LayoutRes
    protected abstract val layoutResId: Int?

    @UiMainThread
    private val lifecyclePluginObservers: MutableList<LifecyclePluginObserver> = mutableListOf()

    @UiMainThread
    protected val viewsHandler = LockableHandler(defaultIsLocked = true)

    override val currentState: Lifecycle.State?
        get() = if (view != null) {
            viewLifecycleOwner.lifecycle.currentState
        } else {
            null
        }

    @CallSuper
    override fun onAttach(context: Context) {
        initLifecyclePluginObservers()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperAttach)
        super.onAttach(context)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperAttach)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperCreate)
        super.onCreate(savedInstanceState)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperCreate)
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperCreateView)

        val localLayoutResId = layoutResId
        return if (localLayoutResId != null) {
            inflater.inflate(localLayoutResId, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperViewCreated)
    }

    @CallSuper
    override fun onStart() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperStart)
        super.onStart()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperStart)
    }

    @CallSuper
    override fun onResume() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperResume)
        super.onResume()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperResume)
    }

    @CallSuper
    override fun onPause() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperPause)
        super.onPause()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperPause)
    }

    @CallSuper
    override fun onStop() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperStop)
        super.onStop()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperStop)
    }

    @CallSuper
    override fun onDestroyView() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDestroyView)
        super.onDestroyView()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDestroyView)
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperSaveInstanceState)
        super.onSaveInstanceState(outState)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperSaveInstanceState)
    }

    @CallSuper
    override fun onDestroy() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDestroy)
        super.onDestroy()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDestroy)
    }

    @CallSuper
    override fun onDetach() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDetach)
        super.onDetach()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDetach)
        lifecyclePluginObservers.clear()
    }

    @CallSuper
    @UiMainThread
    protected open fun initLifecyclePluginObservers() {
        addLifecyclePluginObserver(LockableHandlerLifecyclePluginObserver(viewsHandler = viewsHandler))
    }

    @UiMainThread
    override fun addLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        lifecyclePluginObservers.add(observer)
    }

    @UiMainThread
    override fun removeLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        lifecyclePluginObservers.remove(observer)
    }
}