package com.ruslan.hlushan.core.ui.routing

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver

//TODO: #write_unit_tests
class FlowFragmentLifecyclePluginObserver(
        private val flowCicerone: Cicerone<out Router>,
        private val createFlowNavigator: () -> Navigator
) : LifecyclePluginObserver {

    override fun onAfterSuperResume() =
            flowCicerone.getNavigatorHolder().setNavigator(createFlowNavigator())

    override fun onBeforeSuperPause() =
            flowCicerone.getNavigatorHolder().removeNavigator()
}