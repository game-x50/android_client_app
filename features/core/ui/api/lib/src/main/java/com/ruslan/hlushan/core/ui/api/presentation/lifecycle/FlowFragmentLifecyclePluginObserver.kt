package com.ruslan.hlushan.core.ui.api.presentation.lifecycle

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router

//TODO: #write_unit_tests
internal class FlowFragmentLifecyclePluginObserver(
        private val flowCicerone: Cicerone<out Router>,
        private val createFlowNavigator: () -> Navigator
) : LifecyclePluginObserver {

    override fun onAfterSuperResume() =
            flowCicerone.getNavigatorHolder().setNavigator(createFlowNavigator())

    override fun onBeforeSuperPause() =
            flowCicerone.getNavigatorHolder().removeNavigator()
}