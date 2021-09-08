package com.ruslan.hlushan.core.ui.routing

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen

class FlowRouter(private val parentRouter: Router) : Router() {

    fun startFlow(screen: Screen) = parentRouter.navigateTo(screen)

    fun newRootFlow(screen: Screen) = parentRouter.newRootScreen(screen)

    fun finishFlow() = parentRouter.exit()
}