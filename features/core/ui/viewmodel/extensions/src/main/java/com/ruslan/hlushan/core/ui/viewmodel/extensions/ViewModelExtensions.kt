package com.ruslan.hlushan.core.ui.viewmodel.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.viewmodel.BaseViewModel
import kotlin.reflect.KClass

//TODO: #write_unit_tests
inline fun <Owner, reified VM> Owner.bindBaseViewModel(
        noinline creator: () -> VM
): Lazy<VM> where Owner : ViewModelStoreOwner, Owner : LifecyclePluginObserver.Owner, VM : BaseViewModel =
        ViewModelLazy(
                VM::class,
                owner = this,
                factory = createAbstractViewModelFactory(creator)
        )

class ViewModelLazy<Owner, VM>(
        private val viewModelClass: KClass<VM>,
        owner: Owner,
        factory: ViewModelProvider.Factory
) : Lazy<VM> where Owner : ViewModelStoreOwner, Owner : LifecyclePluginObserver.Owner, VM : BaseViewModel {

    private val lifecyclePluginObserver = object : LifecyclePluginObserver {

        @UiMainThread
        override fun onAfterSuperStart() =
                notifyViewModelOnViewAttached()

        @UiMainThread
        override fun onBeforeSuperStop() =
                notifyViewModelOnViewDetached()
    }

    private val lock = this

    @Volatile
    private var cached: VM? = null

    private var owner: Owner? = owner
    private var factory: ViewModelProvider.Factory? = factory

    override val value: VM
        get() {
            val viewModel1 = cached
            @Suppress("MandatoryBracesIfStatements")
            return if (viewModel1 != null) {
                viewModel1
            } else synchronized(lock) {
                var viewModel2 = cached

                if (viewModel2 == null) {
                    val nonNullFactory: ViewModelProvider.Factory = factory!!
                    val nonNullOwner: Owner = owner!!
                    factory = null
                    owner = null

                    val valueFromProvider = ViewModelProvider(nonNullOwner, nonNullFactory).get(viewModelClass.java)

                    viewModel2 = valueFromProvider

                    cached = valueFromProvider

                    addLifecycleObserver(nonNullOwner)
                }

                return viewModel2
            }
        }

    override fun isInitialized(): Boolean = (cached != null)

    private fun addLifecycleObserver(nonNullOwner: Owner) {
        nonNullOwner.addLifecyclePluginObserver(lifecyclePluginObserver)

        val ownerCurrentState = nonNullOwner.currentState

        if ((ownerCurrentState != null) && (ownerCurrentState >= Lifecycle.State.STARTED)) {
            notifyViewModelOnViewAttached()
        }
    }

    @UiMainThread
    private fun notifyViewModelOnViewAttached() = value.onAfterAttachView()

    @UiMainThread
    private fun notifyViewModelOnViewDetached() = value.onBeforeDetachView()
}

inline fun <reified VM : BaseViewModel> createAbstractViewModelFactory(
        crossinline creator: () -> VM
): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                require(modelClass == VM::class.java)
                @Suppress("UNCHECKED_CAST")
                return (creator() as T)
            }
        }