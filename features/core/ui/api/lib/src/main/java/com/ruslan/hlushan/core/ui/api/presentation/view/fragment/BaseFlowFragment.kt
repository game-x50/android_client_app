package com.ruslan.hlushan.core.ui.api.presentation.view.fragment

import android.os.Bundle
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.R
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.FlowFragmentLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.api.router.FlowCiceronesHolder
import com.ruslan.hlushan.core.ui.api.router.FlowRouter
import com.ruslan.hlushan.extensions.lazyUnsafe
import javax.inject.Inject

abstract class BaseFlowFragment
@ContentView
constructor(
        @LayoutRes layoutResId: Int = R.layout.app_layout_container
) : BaseFragment(layoutResId) {

    @Inject
    lateinit var flowCiceronesHolder: FlowCiceronesHolder

    protected abstract val flowName: String

    val flowCicerone: Cicerone<FlowRouter> by lazyUnsafe { flowCiceronesHolder.getOrCreate(flowName, parentRouter) }

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(FlowFragmentLifecyclePluginObserver(
                flowCicerone = flowCicerone,
                createFlowNavigator = this::createFlowNavigator
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (childFragmentManager.fragments.isEmpty()) {
            openFirstFlowScreen()
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