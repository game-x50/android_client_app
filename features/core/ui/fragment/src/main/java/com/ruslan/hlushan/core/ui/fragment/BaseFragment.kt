package com.ruslan.hlushan.core.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.github.terrakok.cicerone.Router
import com.google.android.material.snackbar.Snackbar
import com.ruslan.hlushan.android.extensions.ViewLambdaListener
import com.ruslan.hlushan.android.extensions.applicationLabel
import com.ruslan.hlushan.android.extensions.executeHideKeyboard
import com.ruslan.hlushan.core.api.managers.CompositeUserErrorMapper
import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.ui.api.R
import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.api.utils.LockableHandler
import com.ruslan.hlushan.core.ui.api.utils.NewIntentHandler
import com.ruslan.hlushan.core.ui.dialog.DialogCommandsHandlerLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.dialog.showDialogMessage
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.lifecycle.dispatchEventForAll
import com.ruslan.hlushan.core.ui.lifecycle.utils.LockableHandlerLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.lifecycle.utils.LoggerLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.routing.CiceroneOwner
import com.ruslan.hlushan.third_party.androidx.material.extensions.showSnackBar
import javax.inject.Inject

@SuppressWarnings("TooManyFunctions")
abstract class BaseFragment
@ContentView
constructor(
        @LayoutRes layoutResId: Int
) : Fragment(layoutResId),
    DialogCommandsHandler.Owner,
    LifecyclePluginObserver.Owner,
    NewIntentHandler {

    @UiMainThread
    private val lifecyclePluginObservers: MutableList<LifecyclePluginObserver> = mutableListOf()

    @Inject
    protected lateinit var resourceManager: ResourceManager

    @Inject
    protected lateinit var appLogger: AppLogger

    @Inject
    protected lateinit var appActivitiesSettings: AppActivitiesSettings

    @Inject
    protected lateinit var compositeUserErrorMapper: CompositeUserErrorMapper

    @Suppress("UnsafeCast")
    protected val parentRouter: Router
        get() = ((parentFragment as? CiceroneOwner) ?: (activity as CiceroneOwner))
                .cicerone
                .router

    @UiMainThread
    private var snackBar: Snackbar? = null

    @UiMainThread
    internal var instanceStateSaved: Boolean = false
        private set

    @Suppress("LeakingThis")
    @UiMainThread
    override val dialogCommandsHandler: DialogCommandsHandler = FragmentDialogCommandsHandler(this)

    @UiMainThread
    protected val viewsHandler = LockableHandler(defaultIsLocked = true)

    override val currentState: Lifecycle.State?
        get() = if (view != null) {
            viewLifecycleOwner.lifecycle.currentState
        } else {
            null
        }

    @UiMainThread
    abstract fun injectDagger2()

    @CallSuper
    override fun onAttach(context: Context) {
        injectDagger2()
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

        if (savedInstanceState != null) {
            restoreFromSavedInstanceState(savedInstanceState)
        }
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperCreateView)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @CallSuper
    @UiMainThread
    protected open fun restoreFromSavedInstanceState(savedInstanceState: Bundle) =
            appLogger.log(this)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperViewCreated)
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) = appLogger.log(this)

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
        instanceStateSaved = false
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperResume)
    }

    @CallSuper
    override fun onPause() {
        hideKeyboard()
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
        instanceStateSaved = true
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperSaveInstanceState)
    }

    @CallSuper
    override fun onDestroy() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDestroy)
        super.onDestroy()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDestroy)

        if (needCloseScope()) {
            onCloseScope()
        }
    }

    @CallSuper
    override fun onDetach() {
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDetach)
        super.onDetach()
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDetach)

        lifecyclePluginObservers.clear()
    }

    @UiMainThread
    protected open fun onBackPressed() = parentRouter.exit()

    @UiMainThread
    protected open fun onCloseScope() = Unit

    @CallSuper
    @UiMainThread
    protected open fun initLifecyclePluginObservers() {
        addLifecyclePluginObserver(LoggerLifecyclePluginObserver(owner = this, appLogger = appLogger))
        addLifecyclePluginObserver(DialogCommandsHandlerLifecyclePluginObserver(
                dialogCommandsHandler = dialogCommandsHandler
        ))
        addLifecyclePluginObserver(LockableHandlerLifecyclePluginObserver(viewsHandler = viewsHandler))
        addLifecyclePluginObserver(AppActivitiesSettingsLifecyclePluginObserver(
                owner = this,
                appActivitiesSettings = appActivitiesSettings
        ))
        addLifecyclePluginObserver(OnBackPressedCallbackLifecyclePluginObserver(
                owner = this,
                onBackPressed = ::onBackPressed
        ))
    }

    @UiMainThread
    override fun addLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        lifecyclePluginObservers.add(observer)
    }

    @UiMainThread
    override fun removeLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        lifecyclePluginObservers.remove(observer)
    }

    @UiMainThread
    fun hideKeyboard() {
        appLogger.log(this)
        activity?.executeHideKeyboard()
    }

    @UiMainThread
    fun showSnackBar(
            message: String,
            actionText: String? = null,
            onActionListener: (() -> Unit)? = null,
            duration: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        appLogger.log(this)
        hideSnackBar()
        hideKeyboard()
        ifNotNull(activity) { nonNullActivity ->
            val listener: View.OnClickListener? = onActionListener?.let { l -> ViewLambdaListener(l) }
            snackBar = view?.showSnackBar(message, actionText, listener, duration)
        }
    }

    @UiMainThread
    fun hideSnackBar() {
        appLogger.log(this)
        snackBar?.dismiss()
        snackBar = null
    }

    @UiMainThread
    fun showError(error: Throwable) = this.showDialogMessage(
            title = context?.applicationLabel.orEmpty(),
            message = compositeUserErrorMapper.produceUserMessage(error = error),
            buttonText = getString(R.string.cancel)
    )

    //This is android, baby!
    @SuppressWarnings("MaxLineLength")
    @UiMainThread
    private fun isRealRemoving(): Boolean =
            (isRemoving && !instanceStateSaved) //because isRemoving == true for fragment in backstack on screen rotation
            || ((parentFragment as? BaseFragment)?.isRealRemoving() ?: false)

    /**It will be valid only for [Fragment.onDestroy] method */
    private fun needCloseScope(): Boolean =
            when {
                (activity?.isChangingConfigurations == true) -> false
                (activity?.isFinishing == true)              -> true
                else                                         -> isRealRemoving()
            }
}