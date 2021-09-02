package com.ruslan.hlushan.work.manager.extensions.di

import androidx.work.WorkerFactory

interface WorkerFactoryProvider {

    fun provideWorkerFactory(): WorkerFactory
}