package com.ruslan.hlushan.core.ui.api.router

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen

/**
 * @author Ruslan Hlushan on 2019-06-26
 */
class FlowRouter(private val parentRouter: Router) : Router() {

    fun startFlow(screen: Screen) = parentRouter.navigateTo(screen)

    fun newRootFlow(screen: Screen) = parentRouter.newRootScreen(screen)

    fun finishFlow() = parentRouter.exit()
}