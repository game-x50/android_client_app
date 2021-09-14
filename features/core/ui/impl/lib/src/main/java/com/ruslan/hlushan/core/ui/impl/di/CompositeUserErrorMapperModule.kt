package com.ruslan.hlushan.core.ui.impl.di

import com.ruslan.hlushan.core.error.CompositeUserErrorMapper
import com.ruslan.hlushan.core.error.SimpleUserErrorMapper
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