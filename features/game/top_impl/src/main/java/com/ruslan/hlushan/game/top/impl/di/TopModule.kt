package com.ruslan.hlushan.game.top.impl.di

import com.ruslan.hlushan.game.core.api.top.TopInteractor
import com.ruslan.hlushan.game.top.impl.TopInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module(includes = [TopRemoteHttpApiModule::class])
internal interface TopModule {

    @Binds
    @Reusable
    fun provideTopInteractor(impl: TopInteractorImpl): TopInteractor
}