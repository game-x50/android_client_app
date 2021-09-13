package com.ruslan.hlushan.core.ui.fragment

import android.os.Bundle
import androidx.annotation.ContentView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.extensions.lazyUnsafe
import com.ruslan.hlushan.core.ui.api.R
import com.ruslan.hlushan.core.ui.api.utils.OnBackPressedHandler
import com.ruslan.hlushan.core.ui.routing.CiceroneOwner
import com.ruslan.hlushan.core.ui.routing.FlowCiceronesHolder
import com.ruslan.hlushan.core.ui.routing.FlowFragmentLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.routing.FlowRouter
import javax.inject.Inject

abstract class BaseFlowFragment
@ContentView
constructor(
        @LayoutRes layoutResId: Int = com.ruslan.hlushan.core.ui.layout.container.R.layout.app_layout_container
) : BaseFragment(layoutResId), CiceroneOwner {

    private val currentFragment: Fragment?
        get() = childFragmentManager.findFragmentById(flowContainerResId)

    @Inject
    lateinit var flowCiceronesHolder: FlowCiceronesHolder

    @get:IdRes
    protected open val flowContainerResId: Int
        get() = com.ruslan.hlushan.core.ui.layout.container.R.id.app_container

    protected abstract val flowName: String

    override val cicerone: Cicerone<FlowRouter> by lazyUnsafe {
        flowCiceronesHolder.getOrCreate(flowName, parentRouter)
    }

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(FlowFragmentLifecyclePluginObserver(
                flowCicerone = cicerone,
                createFlowNavigator = this::createFlowNavigator
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (childFragmentManager.fragments.isEmpty()) {
            openFirstFlowScreen()
        }
    }

    override fun onBackPressed() {
        val handler = (currentFragment as? OnBackPressedHandler)
        if (handler != null) {
            handler.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    @UiMainThread
    override fun onCloseScope() {
        super.onCloseScope()
        flowCiceronesHolder.clear(flowName)
    }

    @UiMainThread
    protected abstract fun createFlowNavigator(): Navigator

    @UiMainThread
    protected abstract fun openFirstFlowScreen()
}