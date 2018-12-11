package com.ruslan.hlushan.core.ui.impl.di

import com.ruslan.hlushan.core.api.managers.CompositeUserErrorMapper
import com.ruslan.hlushan.core.api.managers.SimpleUserErrorMapper
import com.ruslan.hlushan.core.ui.impl.manager.DefaultUiUserErrorProducer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object CompositeUserErrorMapperModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideCompositeUserErrorMapper(
            external: List<SimpleUserErrorMapper>,
            defaultUiUserErrorProducer: DefaultUiUserErrorProducer
    ): CompositeUserErrorMapper =
            CompositeUserErrorMapper(
                    simpleProducers = (external + defaultUiUserErrorProducer)
            )
}