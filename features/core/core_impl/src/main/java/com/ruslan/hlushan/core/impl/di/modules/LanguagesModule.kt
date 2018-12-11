package com.ruslan.hlushan.core.impl.di.modules

import com.ruslan.hlushan.core.api.model.interactors.LanguagesInteractor
import com.ruslan.hlushan.core.impl.model.interactors.LanguagesInteractorImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by User on 06.09.2017.
 */

@Module
internal interface LanguagesModule {
    @Binds
    @Singleton
    fun languagesInteractor(languagesInteractorImpl: LanguagesInteractorImpl): LanguagesInteractor
}