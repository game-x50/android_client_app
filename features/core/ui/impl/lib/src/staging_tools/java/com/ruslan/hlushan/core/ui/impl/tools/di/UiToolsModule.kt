package com.ruslan.hlushan.core.ui.impl.tools.di

import dagger.Module

@Module(
        includes = [
            StagingUiToolsModule::class
        ]
)
internal object UiToolsModule