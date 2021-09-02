package com.ruslan.hlushan.work.manager.utils.di

import androidx.work.WorkerFactory

interface WorkerFactoryProvider {

    fun provideWorkerFactory(): WorkerFactory
}