package com.ruslan.hlushan.game.storage.impl.di

import androidx.work.WorkerFactory
import com.ruslan.hlushan.game.storage.impl.workers.SyncWorker
import dagger.Binds
import dagger.Module

@Module
internal interface WorkersFactoryModule {

    @Binds
    fun provideWorkerFactory(impl: SyncWorker.Factory): WorkerFactory
}