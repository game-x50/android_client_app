package com.ruslan.hlushan.core.ui.routing.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.ui.routing.FlowCiceronesHolder

interface UiRoutingProvider {

    fun provideAppCicerone(): Cicerone<Router>

    fun provideFlowCiceronesHolder(): FlowCiceronesHolder
}