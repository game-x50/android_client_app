package com.ruslan.hlushan.core.ui.viewmodel

import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.api.utils.thread.checkThread
import com.ruslan.hlushan.core.logger.api.AppLogger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReference

abstract class BaseViewModel(
        protected val appLogger: AppLogger,
        private val threadChecker: ThreadChecker
) : ViewModel() {

    private val mutableAttachedViewLifecycleRestartableDisposable = AtomicReference<CompositeDisposable?>()

    private val mutableLifecycleRestartableDisposable = CompositeDisposable()

    @UiMainThread
    private var isFirstLaunch: Boolean = true

    @UiMainThread
    private var vmState: VmState = VmState.CREATED

    init {
        @Suppress("LeakingThis")
        appLogger.log(this)

        threadChecker.checkThread()
    }

    @CallSuper
    @UiMainThread
    open fun onAfterAttachView() {
        appLogger.log(this)

        threadChecker.checkThread()

        checkVmStateAndSet(requiredCurrentState = VmState.CREATED, newState = VmState.ATTACHED)

        mutableAttachedViewLifecycleRestartableDisposable.set(CompositeDisposable())

        if (isFirstLaunch) {
            isFirstLaunch = false

            onAfterFirstAttachView()
        }
    }

    @CallSuper
    @UiMainThread
    open fun onBeforeDetachView() {
        appLogger.log(this)

        threadChecker.checkThread()

        checkVmStateAndSet(requiredCurrentState = VmState.ATTACHED, newState = VmState.CREATED)

        clearLiteCompositeDisposable()
    }

    @CallSuper
    @UiMainThread
    override fun onCleared() {
        appLogger.log(this)

        threadChecker.checkThread()

        checkVmStateAndSet(requiredCurrentState = VmState.CREATED, newState = VmState.DESTROYED)

        clearAttachedViewLifecycleRestartableDisposable()

        super.onCleared()
    }

    @VisibleForTesting
    fun cleanUpAll() {
        if (this.vmState == VmState.ATTACHED) {
            this.onBeforeDetachView()
        }

        if (this.vmState == VmState.CREATED) {
            this.onCleared()
        }
    }

    @CallSuper
    @UiMainThread
    protected open fun onAfterFirstAttachView() {
        appLogger.log(this)

        threadChecker.checkThread()
    }

    @UiMainThread
    private fun checkVmStateAndSet(requiredCurrentState: VmState, newState: VmState) {
        if (vmState == requiredCurrentState) {
            vmState = newState
        } else {
            throw IllegalStateException("required vmState state == $requiredCurrentState, but actual == $vmState")
        }
    }

    private fun clearLiteCompositeDisposable() {
        mutableAttachedViewLifecycleRestartableDisposable.get()?.dispose()
        mutableAttachedViewLifecycleRestartableDisposable.set(null)
    }

    private fun clearAttachedViewLifecycleRestartableDisposable() =
            mutableLifecycleRestartableDisposable.dispose()

    protected fun Disposable.joinWhileViewAttached(): Boolean {
        val liteCompositeDisposable = mutableAttachedViewLifecycleRestartableDisposable.get()

        return if (liteCompositeDisposable != null) {
            liteCompositeDisposable.add(this)
        } else {
            this.dispose()
            false
        }
    }

    protected fun Disposable.joinUntilDestroy(): Boolean = mutableLifecycleRestartableDisposable.add(this)
}

/** [androidx.lifecycle.Lifecycle.State]*/
private enum class VmState {
    /**
     * Destroyed state for a [BaseViewModel] is reached:
     *
     * **right inside** [BaseViewModel.onCleared] call.
     *
     * After this event, this [BaseViewModel] will not dispatch any more events.
     */
    DESTROYED,

    /**
     * Created state for a [BaseViewModel] is reached in two cases:
     *
     * * after [BaseViewModel] instance was created by calling constructor,
     * * but before [BaseViewModel.onAfterAttachView] call;
     * * **right inside** [BaseViewModel.onBeforeDetachView] call.
     */
    CREATED,

    /**
     * Attached state for a [BaseViewModel] is reached:
     *
     * * **right inside** [BaseViewModel.onAfterAttachView] call;
     */
    ATTACHED
}