package com.ruslan.hlushan.core.ui.api.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.routing.FlowCiceronesHolder
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier

//todo:split to 2 interfaces
interface UiCoreProvider {

    fun provideAppCicerone(): Cicerone<Router>

    fun provideFlowCiceronesHolder(): FlowCiceronesHolder

    fun provideAppActivitiesSettings(): AppActivitiesSettings

    fun provideThreadChecker(): ThreadChecker

    fun provideViewModifier(): ViewModifier
}