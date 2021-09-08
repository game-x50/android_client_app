package com.ruslan.hlushan.core.ui.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.lifecycle.utils.LoggerLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.dialog.DialogCommandsHandlerLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.lifecycle.dispatchEventForAll
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(),
                              DialogCommandsHandler.Owner,
                              LifecyclePluginObserver.Owner {

    @UiMainThread
    private val lifecyclePluginObservers: MutableList<LifecyclePluginObserver> = mutableListOf()

    @Inject
    protected lateinit var appActivitiesSettings: AppActivitiesSettings

    @Inject
    protected lateinit var appLogger: AppLogger

    override val currentState: Lifecycle.State get() = lifecycle.currentState

    @Suppress("LeakingThis")
    @UiMainThread
    override val dialogCommandsHandler: DialogCommandsHandler = ActivityDialogCommandsHandler(this)

    @UiMainThread
    internal var instanceStateSaved: Boolean = false
        private set

    @UiMainThread
    protected abstract fun initDagger2()

    @UiMainThread
    protected abstract fun initContentView()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        initDagger2()
        initLifecyclePluginObservers()
        applyWindowTransparencyFlags()

        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperAttach)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperAttach)

        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperCreate)
        super.onCreate(savedInstanceState)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperCreate)

        appActivitiesSettings.changeLangIfNeeded(this)

        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperCreateView)
        initContentView()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperViewCreated)

        if (savedInstanceState == null) {
            appActivitiesSettings.changeThemeIfNeeded()
        }
    }

    @CallSuper
    override fun onRestart() {
        appLogger.log(this)
        super.onRestart()
        appActivitiesSettings.checkLocaleAndRecreateIfNeeded(this)
    }

    @CallSuper
    override fun onStart() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperStart)
        super.onStart()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperStart)
    }

    @CallSuper
    override fun onResumeFragments() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperResume)
        super.onResumeFragments()
        instanceStateSaved = false
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
    override fun onSaveInstanceState(outState: Bundle) {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperSaveInstanceState)
        super.onSaveInstanceState(outState)
        instanceStateSaved = true
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperSaveInstanceState)
    }

    @CallSuper
    override fun onDestroy() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDestroyView)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDestroyView)

        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDestroy)
        super.onDestroy()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDestroy)

        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDetach)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDetach)

        lifecyclePluginObservers.clear()
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        appLogger.log(this)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @UiMainThread
    override fun addLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        lifecyclePluginObservers.add(observer)
    }

    @UiMainThread
    override fun removeLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        lifecyclePluginObservers.remove(observer)
    }

    @CallSuper
    @UiMainThread
    protected open fun initLifecyclePluginObservers() {
        addLifecyclePluginObserver(LoggerLifecyclePluginObserver(owner = this, appLogger = appLogger))
        addLifecyclePluginObserver(DialogCommandsHandlerLifecyclePluginObserver(
                dialogCommandsHandler = dialogCommandsHandler
        ))
    }

    @UiMainThread
    protected open fun applyWindowTransparencyFlags() = Unit
}