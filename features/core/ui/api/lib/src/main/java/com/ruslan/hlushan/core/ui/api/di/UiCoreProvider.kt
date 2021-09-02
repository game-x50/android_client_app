package com.ruslan.hlushan.core.ui.api.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.api.router.FlowCiceronesHolder
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier

/**
 * @author Ruslan Hlushan on 11/1/18.
 */
interface UiCoreProvider {

    fun provideAppCicerone(): Cicerone<Router>

    fun provideFlowCiceronesHolder(): FlowCiceronesHolder

    fun provideAppActivitiesSettings(): AppActivitiesSettings

    fun provideThreadChecker(): ThreadChecker

    fun provideViewModifier(): ViewModifier
}